package com.blaybus.server.controller;

import com.blaybus.server.dto.ResponseFormat;
import com.blaybus.server.dto.request.MyPageRequest.MemberUpdateRequest;
import com.blaybus.server.dto.request.MyPageRequest.AdminUpdateRequest;
import com.blaybus.server.dto.response.MyPageResponse.CareGiverResponse;
import com.blaybus.server.dto.response.MyPageResponse.AdminResponse;
import com.blaybus.server.service.MyPageService;
import com.blaybus.server.service.MyPageWriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/my-page")
@Tags(value = @Tag(name = "MyPageController", description = "Get My Info"))
public class MyPageController {

    private final MyPageService myPageService;
    private final MyPageWriteService myPageWriteService;

    /**
     * 내 정보 조회(요양보호사)
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ResponseFormat<CareGiverResponse>> lookUpMember(
            @PathVariable Long memberId) {
        log.info("memberId: {} 내정보 조회", memberId);
        CareGiverResponse response = myPageService.getCareGiverInfo(memberId);

        ResponseFormat<CareGiverResponse> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "요양보호사 정보가 조회되었습니다.",
                response
        );

        return ResponseEntity.ok(responseFormat);
    }


    /**
     * 내 정보 조회(관리자)
     */
    @GetMapping("/admin/{memberId}")
    public ResponseEntity<ResponseFormat<AdminResponse>> lookUpAdmin(@PathVariable Long memberId) {
        log.info("AdminId: {} 관리자 정보 조회", memberId);
        AdminResponse response = myPageService.getAdminInfo(memberId);

        ResponseFormat<AdminResponse> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "관리자 정보가 조회되었습니다.",
                response
        );

        return ResponseEntity.ok(responseFormat);
    }

    /**
     * 내 정보 수정(요양보호사)
     */
    @PutMapping("/member/{memberId}")
    public ResponseEntity<ResponseFormat<Long>> updateMember(@PathVariable Long memberId,
                                                             @RequestPart("memberUpdateRequest") MemberUpdateRequest memberUpdateRequest,
                                                             @RequestPart(required = false) MultipartFile file) {
        log.info("memberId: {} 요양보호사 정보 수정", memberId);
        Long updateId = myPageWriteService.updateMemberInfo(memberId, memberUpdateRequest, file);

        ResponseFormat<Long> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "요양보호사 정보가 수정되었습니다.",
                updateId
        );

        return ResponseEntity.ok(responseFormat);
    }

    /**
     * 내 정보 수정(관리자)
     */
    @PutMapping("/admin/{memberId}")
    public ResponseEntity<ResponseFormat<Long>> updateAdmin(@PathVariable Long memberId,
                                                            @RequestPart("adminUpdateRequest") AdminUpdateRequest adminUpdateRequest,
                                                            @RequestPart(required = false) MultipartFile file) {
        log.info("AdminId: {} 관리자 정보 수정", memberId);
        Long updateId = myPageWriteService.updateAdminInfo(memberId, adminUpdateRequest, file);

        ResponseFormat<Long> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "관리자 정보가 수정되었습니다.",
                updateId
        );

        return ResponseEntity.ok(responseFormat);
    }

    /**
     * 내 일지 조회(요양보호사 용)
     */

    /**
     * 일지 조회(관리자 용)
     */


}
