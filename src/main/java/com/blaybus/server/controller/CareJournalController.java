package com.blaybus.server.controller;

import com.blaybus.server.domain.journal.CareJournal;
import com.blaybus.server.dto.ResponseFormat;
import com.blaybus.server.dto.request.CareJournalRequest;
import com.blaybus.server.dto.request.CareJournalSearchRequest;
import com.blaybus.server.dto.response.CareJournalResponse;
import com.blaybus.server.service.CareJournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seniors/{seniorId}/care-journals")
@RequiredArgsConstructor
@Tag(name = "CareJournalController", description = "케어일지 관련 API")
public class CareJournalController {

    private final CareJournalService careJournalService;

    @PostMapping
    @Operation(summary = "케어일지 생성", description = "새로운 케어일지를 생성합니다.")
    public ResponseEntity<ResponseFormat<CareJournalResponse>> createCareJournal(
            @PathVariable Long seniorId,
            @RequestBody CareJournalRequest request) {
        CareJournal careJournal = careJournalService.createCareJournal(seniorId, request);
        CareJournalResponse response = CareJournalResponse.fromEntity(careJournal);
        ResponseFormat<CareJournalResponse> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "케어일지가 생성되었습니다.",
                response
        );
        return ResponseEntity.ok(responseFormat);
    }

    @GetMapping
    @Operation(summary = "모든 케어일지 조회", description = "특정 시니어의 모든 케어일지를 조회합니다.")
    public ResponseEntity<ResponseFormat<List<CareJournalResponse>>> getCareJournalsBySeniorId(
            @PathVariable Long seniorId) {
        List<CareJournal> careJournals = careJournalService.getCareJournalsBySeniorId(seniorId);
        List<CareJournalResponse> responseList = careJournals.stream()
                .map(CareJournalResponse::fromEntity)
                .collect(Collectors.toList());
        ResponseFormat<List<CareJournalResponse>> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "해당 시니어의 케어일지가 조회되었습니다.",
                responseList
        );
        return ResponseEntity.ok(responseFormat);
    }

    @GetMapping("/{careJournalId}")
    @Operation(summary = "케어일지 조회", description = "특정 시니어의 특정 케어일지를 조회합니다.")
    public ResponseEntity<ResponseFormat<Optional<CareJournalResponse>>> getCareJournalBySeniorIdAndJournalId(
            @PathVariable Long seniorId,
            @PathVariable Long careJournalId) {
        Optional<CareJournal> careJournal = careJournalService.getCareJournalBySeniorIdAndJournalId(seniorId, careJournalId);
        Optional<CareJournalResponse> response = careJournal.map(CareJournalResponse::fromEntity);
        ResponseFormat<Optional<CareJournalResponse>> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "케어일지가 조회되었습니다.",
                response
        );
        return ResponseEntity.ok(responseFormat);
    }

    @PutMapping("/{careJournalId}")
    @Operation(summary = "케어일지 수정", description = "특정 시니어의 특정 케어일지를 수정합니다.")
    public ResponseEntity<ResponseFormat<CareJournalResponse>> updateCareJournal(
            @PathVariable Long seniorId,
            @PathVariable Long careJournalId,
            @RequestBody CareJournalRequest request) {
        CareJournal updatedCareJournal = careJournalService.updateCareJournal(seniorId, careJournalId, request);
        CareJournalResponse response = CareJournalResponse.fromEntity(updatedCareJournal);
        ResponseFormat<CareJournalResponse> responseFormat = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "케어일지가 수정되었습니다.",
                response
        );
        return ResponseEntity.ok(responseFormat);
    }

    @DeleteMapping("/{careJournalId}")
    @Operation(summary = "케어일지 삭제", description = "특정 시니어의 특정 케어일지를 삭제합니다.")
    public ResponseEntity<ResponseFormat<Void>> deleteCareJournal(
            @PathVariable Long seniorId,
            @PathVariable Long careJournalId) {
        careJournalService.deleteCareJournal(seniorId, careJournalId);
        ResponseFormat<Void> responseFormat = new ResponseFormat<>(
                HttpStatus.NO_CONTENT.value(),
                "케어일지가 삭제되었습니다.",
                null
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseFormat);
    }

    /**
     * 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseFormat<List<CareJournal>>> getCareJournalBy(@ModelAttribute CareJournalSearchRequest careJournalSearchRequest) {
        List<CareJournal> careJournals = careJournalService.searchCareJournal(careJournalSearchRequest);

        ResponseFormat<List<CareJournal>> response = new ResponseFormat<>(
                HttpStatus.OK.value(),
                "케어일지가 검색되었습니다.",
                careJournals
        );

        return ResponseEntity.ok(response);
    }
}