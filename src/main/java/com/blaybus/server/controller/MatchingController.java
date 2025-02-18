package com.blaybus.server.controller;

import com.blaybus.server.dto.ResponseFormat;
import com.blaybus.server.dto.request.MatchingCancellationRequest;
import com.blaybus.server.dto.request.MatchingCreationRequest;
import com.blaybus.server.dto.request.MatchingDecisionRequest;
import com.blaybus.server.dto.response.MatchingResponse;
import com.blaybus.server.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/matching")
@Tag(name = "MatchingController", description = "매칭 요청 관련 API")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * 매칭 요청 생성 (센터에서 요양보호사에게 요청)
     */
    @PostMapping("/requests")
    @Operation(summary = "센터에서 요양보호사에게 매칭 요청")
    public ResponseEntity<ResponseFormat<MatchingResponse>> createMatchingRequest(
            @RequestBody MatchingCreationRequest request) {
        MatchingResponse matchingResponse = matchingService.createMatchingRequest(request);
        ResponseFormat<MatchingResponse> response = new ResponseFormat<>(
                HttpStatus.CREATED.value(),
                "매칭 요청이 생성되었습니다.",
                matchingResponse
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 매칭 요청 취소 (센터에서 매칭 요청을 취소)
     */
    @PostMapping("/requests/cancel")
    @Operation(summary = "센터, 요양보호사가 매칭 요청을 취소 혹은 거절")
    public ResponseEntity<ResponseFormat<Void>> cancelMatchingRequest(
            @RequestBody MatchingCancellationRequest request) {
        matchingService.cancelMatchingRequest(request);
        ResponseFormat<Void> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "매칭 요청이 취소되었습니다.",
                null
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 매칭 요청 응답 (요양보호사가 수락/거절/조율 요청)
     */
    @PostMapping("/response")
    @Operation(summary = "요양보호사가 매칭에 대해 수락/거절/조율 응답")
    public ResponseEntity<ResponseFormat<MatchingResponse>> respondToMatchingRequest(
            @RequestBody MatchingDecisionRequest request) {
        MatchingResponse matchingResponse = matchingService.respondToMatchingRequest(request);
        ResponseFormat<MatchingResponse> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "매칭 요청에 대한 응답이 처리되었습니다.",
                matchingResponse
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 요양보호사에게 온 매칭 요청 목록 조회
     */
    @GetMapping("/requests")
    @Operation(summary = "특정 요양보호사에게 온 매칭 요청 목록 조회")
    public ResponseEntity<ResponseFormat<List<MatchingResponse>>> getMatchingRequestsForCareGiver(
            @RequestParam Long caregiverId) {
        List<MatchingResponse> responses = matchingService.getMatchingRequestsForCareGiver(caregiverId)
                .stream()
                .map(MatchingResponse::from)
                .collect(Collectors.toList());
        ResponseFormat<List<MatchingResponse>> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "요양보호사에게 온 매칭 요청 목록입니다.",
                responses
        );
        return ResponseEntity.ok(response);
    }
}
