package com.blaybus.server.service;

import com.blaybus.server.common.Validator;
import com.blaybus.server.common.exception.CareLinkException;
import com.blaybus.server.common.exception.ErrorCode;
import com.blaybus.server.config.security.jwt.JwtUtils;
import com.blaybus.server.domain.*;
import com.blaybus.server.dto.request.AdminRequest;
import com.blaybus.server.dto.request.CareGiverRequest;
import com.blaybus.server.dto.request.LoginRequest;
import com.blaybus.server.dto.request.SignUpRequest;
import com.blaybus.server.dto.response.JwtDto.JwtResponse;
import com.blaybus.server.repository.CenterRepository;
import com.blaybus.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final Validator validator;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final CenterRepository centerRepository;
    private final S3Service s3Service;

    public Long joinMember(SignUpRequest signUpRequest) {
        log.info("join Member: {}", signUpRequest.getEmail());

        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CareLinkException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Member newMember = createCareGiver(signUpRequest);
        memberRepository.save(newMember);
        log.info("Successfully create CareGiver: {}", signUpRequest.getEmail());

        return newMember.getId();
    }

    /**
     * 요양보호사 정보 수정 (업데이트)
     * 프로필 사진 파일이 있으면 S3에 업로드 후 URL 업데이트
     */
    public Long updateCareGiver(CareGiverRequest request, MultipartFile profilePicture) {
        log.info("Updating CareGiver info for email: {}", request.getEmail());
        CareGiver careGiver = (CareGiver) memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND));

        validateCareGiverRequest(request);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String profileUrl = s3Service.uploadFile(profilePicture);
            log.info("profile url: {}", profileUrl);
            careGiver.updateCareGiverInfo(request, profileUrl);
        } else {
            careGiver.updateCareGiverInfo(request);
            log.info("no profile picture");
        }


        memberRepository.save(careGiver);
        log.info("Successfully updated CareGiver: {}", careGiver.getEmail());
        return careGiver.getId();
    }

    public Long joinAdmin(AdminRequest request) {
        log.info("join Admin: {}", request.getEmail());

        validateSignupRequest(request);

        Member admin = createAdmin(request);

        memberRepository.save(admin);

        log.info("Successfully join Admin: {}", admin.getEmail());

        return admin.getId();
    }

    public JwtResponse loginMember(LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (!memberRepository.existsByEmail(username)) {
            throw new CareLinkException(ErrorCode.USER_NOT_FOUND);
        }

        log.info("validateUser: {}, {}", username, password);
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );


        final String jwtToken = jwtUtils.generateJwtToken(authentication);
        final String refreshToken = jwtUtils.generateRefreshToken(authentication);

        final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        return JwtResponse.createJwtResponse(jwtToken, refreshToken, userDetails.getUsername(), roles);
    }

    private void validateSignupRequest(SignUpRequest request) {
        if (!validator.checkPassword(request.getPassword(), request.getConfirmPassword())) {
            throw new CareLinkException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!validator.checkRole(request.getType())) {
            throw new CareLinkException(ErrorCode.INVALID_TYPE);
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CareLinkException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    private CareGiver createCareGiver(SignUpRequest signUpRequest) {
        validateSignupRequest(signUpRequest);

        return new CareGiver(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                LoginType.LOCAL,
                signUpRequest.getGenderType(),
                signUpRequest.getName()
        );
    }

    private void validateCareGiverRequest(CareGiverRequest caregiverInfo) {
        String certificateNumber = caregiverInfo.getCertificateNumber();
        CareGiverType careGiverType = caregiverInfo.getCareGiverType();

        if (careGiverType == CareGiverType.CAREWORKER) { // 🚀 1️⃣ 요양보호사: 자격증 번호 형식 검증
            if (!certificateNumber.matches("^[0-9]{9}[A-Z]$")) {
                throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_NUMBER_CAREWORKER);
            }
        } else if (careGiverType == CareGiverType.SOCIALWORKER) { // 🚀 2️⃣ 사회복지사: 자격증 번호 형식 검증
            if (!certificateNumber.matches("^[12]-[0-9]{5,6}$")) {
                throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_NUMBER_SOCIALWORKER);
            }
        } else if (careGiverType == CareGiverType.NURSINGASSISTANT) { // 🚀 3️⃣ 간호조무사: 자격증 번호 형식 검증
            if (!certificateNumber.matches("^[12]-[0-9]{5,6}$")) {
                throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_NUMBER_NURSINGASSISTANT);
            }
        } else { // 🚀 4️⃣ 알 수 없는 자격증 종류
            throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_TYPE);
        }
    }


    private Admin createAdmin(AdminRequest adminRequest) {
        // 1️⃣ 센터 존재 여부 확인
        Center center = centerRepository.findById(adminRequest.getCenterId())
                .orElseThrow(() -> new CareLinkException(ErrorCode.CENTER_NOT_FOUND));

        // 2️⃣ Admin 객체 생성
        return new Admin(
                adminRequest.getEmail(),
                passwordEncoder.encode(adminRequest.getPassword()),
                LoginType.LOCAL,
                adminRequest.getName(),
                center,
                adminRequest.getContactNumber(),
                adminRequest.getIntroduction(),
                adminRequest.getProfilePictureUrl(),
                adminRequest.getAdminType()
        );
    }


}
