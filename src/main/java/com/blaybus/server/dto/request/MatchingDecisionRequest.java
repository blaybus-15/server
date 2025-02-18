package com.blaybus.server.dto.request;

import com.blaybus.server.domain.Day;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Builder
public class MatchingDecisionRequest {

    @NotNull
    private Long matchingRequestId;

    @NotNull
    private CareGiverResponseStatus response;

    // 조율 요청 시 새롭게 제안할 근무 요일 (옵션)
    private Set<Day> newVisitDays;

    // 조율 요청 시 새롭게 제안할 근무 시작 시간 (옵션)
    private LocalTime newStartTime;

    // 조율 요청 시 새롭게 제안할 근무 종료 시간 (옵션)
    private LocalTime newEndTime;
}
