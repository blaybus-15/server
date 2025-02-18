package com.blaybus.server.dto.request;

import com.blaybus.server.domain.Day;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Builder
public class MatchingCreationRequest {

    @NotNull
    private Long seniorId;         // 매칭할 어르신의 ID

    @NotNull
    private Long caregiverId;      // 매칭 대상 요양보호사의 ID

    @NotNull
    private Integer wage;          // 제안 임금 (예: 시급)

    @NotNull
    private Set<Day> visitDays;    // 근무 요일 (예: [MONDAY, WEDNESDAY, FRIDAY])

    @NotNull
    private LocalTime startTime;   // 근무 시작 시간 (HH:mm 형식)

    @NotNull
    private LocalTime endTime;     // 근무 종료 시간 (HH:mm 형식)
}
