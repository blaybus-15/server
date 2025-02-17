package com.blaybus.server.controller;

import com.blaybus.server.dto.ResponseFormat;
import com.blaybus.server.dto.request.AdminRequest;
import com.blaybus.server.dto.request.CareGiverRequest;
import com.blaybus.server.dto.request.LoginRequest;
import com.blaybus.server.dto.request.SignUpRequest;
import com.blaybus.server.dto.response.JwtDto.JwtResponse;
import com.blaybus.server.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tags(value = @Tag(name = "AccountController", description = "Certification and Accreditation"))
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/sign-up")
    @Operation(summary = "Sign up as a new member or admin")
    public ResponseEntity<ResponseFormat<Long>> signUpMember(@RequestBody @Valid SignUpRequest signUpRequest) {
        log.info("member created");
        Long createdEmail = accountService.joinMember(signUpRequest);

        ResponseFormat<Long> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "멤버가 성공적으로 등록되었습니다. (email: " + createdEmail + ")",
                createdEmail
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 내 정보 수정 (요양보호사)
     * JSON과 프로필 사진 파일을 함께 받음.
     */
    @PutMapping(value = "/sign-up/member", consumes = {"multipart/form-data"})
    @Operation(summary = "Update Member Info")
    public ResponseEntity<ResponseFormat<Long>> signUpMember(
            @RequestPart("careGiverRequest") @Valid CareGiverRequest careGiverRequest,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        Long createdId = accountService.updateCareGiver(careGiverRequest, profilePicture);

        ResponseFormat<Long> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "멤버의 프로필이 성공적으로 등록되었습니다. (id: " + createdId + ")",
                createdId
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-up/admin")
    @Operation(summary = "Update Admin Info")
    public ResponseEntity<ResponseFormat<Long>> signUpAdmin(@RequestBody @Valid AdminRequest adminRequest) {
        log.info("Admin created");
        Long createdEmail = accountService.joinAdmin(adminRequest);

        ResponseFormat<Long> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "멤버가 성공적으로 등록되었습니다. (email: " + createdEmail + ")",
                createdEmail
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in as an existing user")
    public ResponseEntity<ResponseFormat<JwtResponse>> signIn(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("member sign in");
        JwtResponse loginResult = accountService.loginMember(loginRequest);

        ResponseFormat<JwtResponse> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "성공적으로 로그인 되었습니다.",
                loginResult
        );

        return ResponseEntity.ok(response);
    }
}
