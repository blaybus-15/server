package com.blaybus.server.service;

import com.blaybus.server.common.exception.CareLinkException;
import com.blaybus.server.common.exception.ErrorCode;
import com.blaybus.server.config.security.jwt.JwtUtils;
import com.blaybus.server.domain.Admin;
import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.Member;
import com.blaybus.server.domain.senior.Senior;
import com.blaybus.server.dto.request.SeniorRequest;
import com.blaybus.server.dto.response.SeniorResponse;
import com.blaybus.server.repository.MemberRepository;
import com.blaybus.server.repository.SeniorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SeniorService {

    private static final int SERIAL_NUMBER_LENGTH = 13;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SeniorRepository seniorRepository;
    private final JwtUtils jwtUtils;
    private final MemberRepository memberRepository;


    /**
     * 어르신 등록 (Create)
     */
    public SeniorResponse createSenior(String token, SeniorRequest request) {
        log.info("Create Senior: {}", request.getName());

        Center center = getCenterByAdminToken(token);

        // 랜덤한 13자리 시리얼 넘버 생성 (중복 체크)
        String serialNumber = generateUniqueSerialNumber();

        Senior senior = Senior.builder()
                .serialNumber(serialNumber)
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .genderType(request.getGenderType())
                .careLevel(request.getCareLevel())
                .address(request.getAddress())
                .contactInfo(request.getContactInfo())
                .guardianContact(request.getGuardianContact())
                .visitType(request.getVisitType())
                .visitDays(request.getVisitDays())
                .visitDates(request.getVisitDates())
                .careStartTime(request.getCareStartTime())
                .careEndTime(request.getCareEndTime())
                .mobilityLevel(request.getMobilityLevel())
                .eatingLevel(request.getEatingLevel())
                .medicalConditions(request.getMedicalConditions())
                .requiredServices(request.getRequiredServices())
                .additionalNotes(request.getAdditionalNotes())
                .center(center)
                .build();

        Senior savedSenior = seniorRepository.save(senior);
        log.info("Successfully created Senior: {} with SerialNumber: {}", request.getName(), serialNumber);

        return SeniorResponse.from(savedSenior);
    }

    /**
     * ID 기반 어르신 단일 조회 (Read)
     */
    public SeniorResponse getSeniorById(Long id) {
        log.info("Get Senior by ID: {}", id);
        Senior senior = seniorRepository.findById(id)
                .orElseThrow(() -> new CareLinkException(ErrorCode.SENIOR_NOT_FOUND));
        return SeniorResponse.from(senior);
    }

    /**
     * 어드민이 소속된 센터의 모든 어르신 정보 조회 (Read)
     * @param token 액세스 토큰 (Authorization 헤더에서 전달, Bearer 토큰)
     * @return 해당 센터에 속한 어르신 정보 목록
     */
    public List<SeniorResponse> getSeniorsByAdmin(String token) {
        Center center = getCenterByAdminToken(token);

        // 해당 센터에 속한 어르신 정보 조회
        List<Senior> seniors = seniorRepository.findByCenter(center);
        return seniors.stream()
                .map(SeniorResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 전체 어르신 목록 조회 (Read)
     */
    public List<SeniorResponse> getAllSeniors() {
        log.info("Get all Seniors");
        return seniorRepository.findAll()
                .stream()
                .map(SeniorResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 어르신 정보 수정 (Update)
     */
    public SeniorResponse updateSenior(Long id, SeniorRequest request) {
        log.info("Update Senior ID: {}", id);
        Senior senior = seniorRepository.findById(id)
                .orElseThrow(() -> new CareLinkException(ErrorCode.SENIOR_NOT_FOUND));
        senior.updateSeniorPartial(request);
        Senior updatedSenior = seniorRepository.save(senior);
        log.info("Successfully updated Senior ID: {}", id);
        return SeniorResponse.from(updatedSenior);
    }

    /**
     * 어르신 삭제 (Delete)
     */
    public void deleteSenior(Long id) {
        log.info("Delete Senior ID: {}", id);
        Senior senior = seniorRepository.findById(id)
                .orElseThrow(() -> new CareLinkException(ErrorCode.SENIOR_NOT_FOUND));
        seniorRepository.delete(senior);
        log.info("Successfully deleted Senior ID: {}", id);
    }

    private Center getCenterByAdminToken(String token) {
        // JWT에서 "id" 클레임 추출 (문자열이므로 Long으로 변환)
        String idStr = jwtUtils.getMemberIdByToken(token);
        Long adminId;
        try {
            adminId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new CareLinkException(ErrorCode.INVALID_ADMIN_IDENTIFIER);
        }

        // MemberRepository로 admin 정보 조회
        Member member = memberRepository.findById(adminId)
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND));
        if (!(member instanceof Admin)) {
            throw new CareLinkException(ErrorCode.NOT_ADMIN);
        }
        Admin admin = (Admin) member;
        return admin.getCenter();
    }

    /**
     * 13자리 랜덤 시리얼 넘버 생성 (중복 방지)
     */
    private String generateUniqueSerialNumber() {
        String serialNumber;
        do {
            serialNumber = generateRandomSerialNumber();
        } while (seniorRepository.existsBySerialNumber(serialNumber));
        return serialNumber;
    }

    private String generateRandomSerialNumber() {
        StringBuilder sb = new StringBuilder(SERIAL_NUMBER_LENGTH);
        for (int i = 0; i < SERIAL_NUMBER_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
