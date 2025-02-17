package com.blaybus.server.service;

import com.blaybus.server.common.Validator;
import com.blaybus.server.common.exception.CareLinkException;
import com.blaybus.server.common.exception.ErrorCode;
import com.blaybus.server.config.security.jwt.JwtUtils;
import com.blaybus.server.domain.*;
import com.blaybus.server.domain.auth.*;
import com.blaybus.server.domain.auth.Experience;
import com.blaybus.server.dto.request.*;
import com.blaybus.server.dto.request.CareGiverCertificate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    public Long joinMember(SignUpRequest signUpRequest, MultipartFile file) {
        log.info("join Member: {}", signUpRequest.getEmail());

        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CareLinkException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String profileUrl = null;
        if (file != null && !file.isEmpty()) {
            profileUrl = s3Service.uploadFile(file);
        }

        Member newMember = createCareGiver(signUpRequest, profileUrl);
        memberRepository.save(newMember);
        log.info("Successfully create CareGiver: {}", signUpRequest.getEmail());

        return newMember.getId();
    }

    public Long saveMemberInfo(CareGiverSocialRequest request, MultipartFile file) {
        log.info("소셜 회원가입 시 멤버 정보 등록: {}", request.getEmail());
        CareGiver careGiver = (CareGiver) memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND));

        String profileUrl = null;
        if (file != null && !file.isEmpty()) {
            profileUrl = s3Service.uploadFile(file);
        }

        careGiver.saveBySocial(request.getGenderType(), request.getName(), request.getContactNumber(), profileUrl);
        memberRepository.save(careGiver);
        log.info("성공적으로 소셜로그인 이후 정보 등록 완료: {}", careGiver.getId());

        return careGiver.getId();
    }

    /**
     * 요양보호사 정보 수정 (업데이트)
     */
    public Long updateCareGiver(CareGiverRequest request) {
        log.info("Updating CareGiver info for email: {}", request.getEmail());
        CareGiver careGiver = (CareGiver) memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND));

        validateCareGiverRequest(request);

        List<Experience> experiences = new ArrayList<>();
        if (request.getExperiences() != null) {
            experiences.addAll(
                    request.getExperiences().stream()
                            .map(exp -> {
                                Center center = centerRepository.findById(exp.getCenterId())
                                        .orElseThrow(() -> new CareLinkException(ErrorCode.CENTER_NOT_FOUND)); // 🚀 Center 조회

                                return new Experience(careGiver, center, exp.getCertificatedAt(), exp.getEndCertificatedAt(), exp.getAssignedTask());
                            })
                            .collect(Collectors.toList())
            );
        }
        careGiver.updateCareGiverInfo(request, experiences);

        memberRepository.save(careGiver);
        log.info("Successfully updated CareGiver: {}", careGiver.getEmail());
        return careGiver.getId();
    }

    public Long joinAdmin(AdminRequest request, MultipartFile profilePicture) {
        log.info("join Admin: {}", request.getEmail());

        validateSignupRequest(request.getEmail(), request.getPassword(), request.getConfirmPassword());

        String profileUrl = null;
        if (profilePicture != null && !profilePicture.isEmpty()) {
            profileUrl = s3Service.uploadFile(profilePicture);
        }

        Member admin = createAdmin(request, profileUrl);

        memberRepository.save(admin);

        log.info("Successfully join Admin: {}", admin.getEmail());

        return admin.getId();
    }

    public Long saveAdminInfo(AdminSocialRequest request, MultipartFile profilePicture) {
        log.info("소셜 회원가입 시 관리자 정보 등록: {}", request.getEmail());
        Admin admin = (Admin) memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND));

        Center center = centerRepository.findById(request.getCenterId())
                .orElseThrow(() -> new CareLinkException(ErrorCode.CENTER_NOT_FOUND));

        String profileUrl = null;
        if (profilePicture != null && !profilePicture.isEmpty()) {
            profileUrl = s3Service.uploadFile(profilePicture);
        }

        admin.saveBySocial(center, request.getName(), request.getContactNumber(), request.getIntroduction(), profileUrl);
        memberRepository.save(admin);
        log.info("성공적으로 소셜로그인 이후 정보 등록 완료: {}", admin.getId());

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

    private void validateSignupRequest(String email, String password, String confirmPassword) {
        if (!validator.checkPassword(password, confirmPassword)) {
            throw new CareLinkException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (memberRepository.existsByEmail(email)) {
            throw new CareLinkException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    private CareGiver createCareGiver(SignUpRequest signUpRequest, String profileUrl) {
        validateSignupRequest(signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getConfirmPassword());

        return new CareGiver(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                LoginType.LOCAL,
                signUpRequest.getGenderType(),
                signUpRequest.getName(),
                profileUrl
        );
    }

    private void validateCareGiverRequest(CareGiverRequest caregiverInfo) {
        String certificateNumber = caregiverInfo.getCertificateNumber();

        // 🚀 1️⃣ 요양보호사 자격증 검증 (필수)
        if (!certificateNumber.matches("^20\\d{2}-\\d{7}$")) {
            throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_NUMBER_CAREWORKER);
        }

        List<CareGiverCertificate> certificates = caregiverInfo.getCertificates();
        for (CareGiverCertificate certificate : certificates) {
            String certNumber = certificate.getCertificatedNumber();
            CareGiverType careGiverType = certificate.getCareGiverType();

            if (careGiverType == CareGiverType.SOCIALWORKER) {
                // 사회복지사 자격증 번호 검증
                if (!certNumber.matches("^[12]-[0-9]{5,6}$")) {
                    throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_NUMBER_SOCIALWORKER);
                }
            } else if (careGiverType == CareGiverType.NURSINGASSISTANT) {
                // 간호조무사 자격증 번호 검증
                if (!certNumber.matches("^(\\d{4}-\\d{5})|(0000-\\d{5})$") &&
                        !certNumber.matches("^\\d{5,6}$")) {
                    throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_NUMBER_NURSINGASSISTANT);
                }
            } else {
                // 🚀 3️⃣ 알 수 없는 자격증 종류 예외 처리
                throw new CareLinkException(ErrorCode.INVALID_CERTIFICATE_TYPE);
            }
        }
    }


    private Admin createAdmin(AdminRequest adminRequest, String profileUrl) {
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
                profileUrl
        );
    }


}
