package com.blaybus.server.dto.response;

import com.blaybus.server.domain.journal.CareJournal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CareJournalResponse {
    private Long id;
    private String careGiverName;
    private LocalDateTime createdAt;
    private LocalDate seniorBirthday;
    private String seniorGender;
    private String seniorCareLevel;

    public static CareJournalResponse fromEntity(CareJournal careJournal) {
        return CareJournalResponse.builder()
                .id(careJournal.getId())
                .careGiverName(careJournal.getCareGiverName())
                .createdAt(careJournal.getCreatedAt())
                .seniorBirthday(careJournal.getSeniorBirthday())
                .seniorGender(careJournal.getSeniorGender())
                .seniorCareLevel(careJournal.getSeniorCareLevel())
                .build();
    }
}