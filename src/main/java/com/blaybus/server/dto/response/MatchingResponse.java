package com.blaybus.server.dto.response;

import com.blaybus.server.domain.matching.MatchingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class MatchingResponse {
    private Long id;
    private Long seniorId;
    private Long caregiverId;
    private Integer wage;
    private Set<String> visitDays;
    private LocalTime startTime;
    private LocalTime endTime;
    private MatchingStatus status;
    private LocalDateTime createdAt;

    public static MatchingResponse from(com.blaybus.server.domain.matching.Matching matching) {
        return MatchingResponse.builder()
                .id(matching.getId())
                .seniorId(matching.getSenior().getId())
                .caregiverId(matching.getCaregiver().getId())
                .wage(matching.getWage())
                .visitDays(matching.getVisitDays().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .startTime(matching.getStartTime())
                .endTime(matching.getEndTime())
                .status(matching.getStatus())
                .createdAt(matching.getCreatedAt())
                .build();
    }
}
