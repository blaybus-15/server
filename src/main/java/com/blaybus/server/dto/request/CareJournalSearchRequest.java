package com.blaybus.server.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CareJournalSearchRequest {
    private String seniorName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
