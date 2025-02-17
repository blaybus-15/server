package com.blaybus.server.controller;

import com.blaybus.server.dto.ResponseFormat;
import com.blaybus.server.dto.request.SeniorRequest;
import com.blaybus.server.dto.response.SeniorResponse;
import com.blaybus.server.service.SeniorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/seniors")
@Tag(name = "SeniorController", description = "어르신 API")
@RequiredArgsConstructor
public class SeniorController {

    private final SeniorService seniorService;

    /**
     * 어르신 등록 (Create)
     */
    @PostMapping
    public ResponseEntity<ResponseFormat<SeniorResponse>> createSenior(
            @RequestHeader("Authorization") String token,
            @RequestBody SeniorRequest request) {
        log.info("Received request to create Senior: {}", request.getName());
        // Bearer 접두어 제거
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        SeniorResponse response = seniorService.createSenior(jwt, request);
        ResponseFormat<SeniorResponse> responseFormat = new ResponseFormat<>(
                HttpStatus.CREATED.value(),
                "어르신 정보가 생성되었습니다.",
                response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseFormat);
    }

    /**
     * ID 기반 어르신 단일 조회 (Read)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseFormat<SeniorResponse>> getSeniorById(@PathVariable Long id) {
        log.info("Received request to get Senior with ID: {}", id);
        SeniorResponse response = seniorService.getSeniorById(id);
        ResponseFormat<SeniorResponse> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "어르신 정보가 조회되었습니다.",
                response
        );
        return ResponseEntity.ok(responseFormat);
    }

    /**
     * 어드민이 소속된 센터의 어르신 목록 조회 (Read)
     * Authorization 헤더에서 JWT 토큰을 받아 해당 토큰의 id 클레임을 기반으로 센터를 조회합니다.
     */
    @GetMapping("/my-center")
    public ResponseEntity<ResponseFormat<List<SeniorResponse>>> getSeniorsByCenter(
            @RequestHeader("Authorization") String token) {
        log.info("Received request to get Seniors for admin's center");
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        List<SeniorResponse> responses = seniorService.getSeniorsByAdmin(jwt);
        ResponseFormat<List<SeniorResponse>> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "해당 센터의 어르신 정보가 조회되었습니다.",
                responses
        );
        return ResponseEntity.ok(responseFormat);
    }

    /**
     * 전체 어르신 목록 조회 (Read)
     */
    @GetMapping
    public ResponseEntity<ResponseFormat<List<SeniorResponse>>> getAllSeniors() {
        log.info("Received request to get all Seniors");
        List<SeniorResponse> responses = seniorService.getAllSeniors();
        ResponseFormat<List<SeniorResponse>> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "모든 어르신 정보가 조회되었습니다.",
                responses
        );
        return ResponseEntity.ok(responseFormat);
    }

    /**
     * 어르신 정보 일부 수정 (Update - Partial)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseFormat<SeniorResponse>> updateSenior(@PathVariable Long id,
                                                                       @RequestBody SeniorRequest request) {
        log.info("Received request to update Senior with ID: {}", id);
        SeniorResponse response = seniorService.updateSenior(id, request);
        ResponseFormat<SeniorResponse> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "어르신 정보가 수정되었습니다.",
                response
        );
        return ResponseEntity.ok(responseFormat);
    }

    /**
     * 어르신 삭제 (Delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseFormat<Void>> deleteSenior(@PathVariable Long id) {
        log.info("Received request to delete Senior with ID: {}", id);
        seniorService.deleteSenior(id);
        ResponseFormat<Void> responseFormat = new ResponseFormat<>(
                HttpStatus.NO_CONTENT.value(),
                "어르신 정보가 삭제되었습니다.",
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseFormat);
    }
}
