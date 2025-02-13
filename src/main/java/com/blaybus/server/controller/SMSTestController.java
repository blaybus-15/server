package com.blaybus.server.controller;

import com.blaybus.server.service.SMSService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/sms")
@Tag(name = "SMSTestController", description = "SMS 전송 테스트 컨트롤러")
public class SMSTestController {

    private final SMSService smsService;

    public static class SMSRequest {
        public String phoneNumber;
        public String message;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSMS(@RequestBody SMSRequest request) {
        try {
            smsService.sendSMS(request.phoneNumber, request.message);
            return ResponseEntity.ok("SMS 전송 성공");
        } catch (Exception e) {
            log.error("SMS 전송 실패: ", e);
            return ResponseEntity.status(500).body("SMS 전송 실패");
        }
    }
}
