package com.blaybus.server.service;

import com.blaybus.server.common.exception.CareLinkException;
import com.blaybus.server.common.exception.ErrorCode;
import com.blaybus.server.domain.matching.Matching;
import com.blaybus.server.domain.matching.MatchingStatus;
import com.blaybus.server.domain.senior.Senior;
import com.blaybus.server.domain.auth.CareGiver;
import com.blaybus.server.dto.request.MatchingCancellationRequest;
import com.blaybus.server.dto.request.MatchingCreationRequest;
import com.blaybus.server.dto.request.MatchingDecisionRequest;
import com.blaybus.server.dto.response.MatchingResponse;
import com.blaybus.server.repository.MatchingRepository;
import com.blaybus.server.repository.SeniorRepository;
import com.blaybus.server.repository.CareGiverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final SeniorRepository seniorRepository;
    private final CareGiverRepository caregiverRepository;
    private final SMSService smsService;

    /**
     * 센터에서 특정 어르신과 특정 요양보호사에게 매칭 요청을 생성합니다.
     */
    public MatchingResponse createMatchingRequest(MatchingCreationRequest request) {
        log.info("Creating Matching Request for Senior ID: {} and CareGiver ID: {}",
                request.getSeniorId(), request.getCaregiverId());

        Senior senior = seniorRepository.findById(request.getSeniorId())
                .orElseThrow(() -> new CareLinkException(ErrorCode.SENIOR_NOT_FOUND));

        CareGiver caregiver = caregiverRepository.findById(request.getCaregiverId())
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND)); // 또는 CAREGIVER_NOT_FOUND

        Matching matching = Matching.builder()
                .senior(senior)
                .caregiver(caregiver)
                .wage(request.getWage())
                .visitDays(request.getVisitDays())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(MatchingStatus.PROGRESS)
                .build();

        Matching savedMatching = matchingRepository.save(matching);
        log.info("Matching Request created with ID: {}", savedMatching.getId());

        String caregiverPhone = caregiver.getContactNumber();
        String messageBody = "새로운 매칭 요청이 도착했습니다. 어르신: " + senior.getName();
        smsService.sendSMS(caregiverPhone, messageBody);
        log.info("SMS sent to caregiver: {}", caregiverPhone);

        return MatchingResponse.from(savedMatching);
    }

    /**
     * 센터에서 매칭 요청을 취소합니다.
     */
    public void cancelMatchingRequest(MatchingCancellationRequest request) {
        log.info("Canceling Matching Request with ID: {}. Reason: {}",
                request.getMatchingRequestId(), request.getCancellationReason());

        Matching matching = matchingRepository.findById(request.getMatchingRequestId())
                .orElseThrow(() -> new CareLinkException(ErrorCode.SENIOR_NOT_FOUND)); // 또는 MATCHING_NOT_FOUND 코드

        matching.updateStatus(MatchingStatus.CANCELED, request.getCancellationReason());
        matchingRepository.save(matching);

        log.info("Matching Request with ID: {} is canceled", request.getMatchingRequestId());
    }

    /**
     * 특정 요양보호사에게 온 매칭 요청 목록 조회
     */
    public List<Matching> getMatchingRequestsForCareGiver(Long caregiverId) {
        return matchingRepository.findByCaregiverId(caregiverId);
    }

    /**
     * 요양보호사가 매칭 요청에 대해 응답(수락/거절/조율 요청)을 처리합니다.
     */
    public MatchingResponse respondToMatchingRequest(MatchingDecisionRequest request) {
        log.info("Responding to Matching Request with ID: {}", request.getMatchingRequestId());
        Matching matching = matchingRepository.findById(request.getMatchingRequestId())
                .orElseThrow(() -> new CareLinkException(ErrorCode.MATCHING_NOT_FOUND)); // 또는

        // CareGiverResponseStatus를 기반으로 상태를 업데이트합니다.
        // TODO: SMS
        switch (request.getResponse()) {
            case ACCEPT:
                matching.updateStatus(MatchingStatus.MATCHED, null);
                break;
            case REJECT:
                matching.updateStatus(MatchingStatus.CANCELED, null);
                break;
            case NEGOTIATE:
                // 조율 요청이면, 새로운 근무 조건(근무 요일, 시작/종료 시간)을 업데이트 후 상태 변경
                matching.updateNegotiationDetails(request.getNewVisitDays(), request.getNewStartTime(), request.getNewEndTime());
                matching.updateStatus(MatchingStatus.NEGOTIATING, null);
                break;
            default:
                throw new CareLinkException(ErrorCode.INVALID_RESPONSE_STATUS_TYPE);
        }

        Matching updatedMatching = matchingRepository.save(matching);
        log.info("Matching Request with ID: {} updated with response: {}", updatedMatching.getId(), request.getResponse());
        return MatchingResponse.from(updatedMatching);
    }

    // 필요에 따라 추가 상태 변경 로직 (수락, 거절, 조율 등) 구현
}
