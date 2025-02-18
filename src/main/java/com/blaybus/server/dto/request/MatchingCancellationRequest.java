package com.blaybus.server.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchingCancellationRequest {
    @NotNull
    private Long matchingRequestId;
    private String cancellationReason;
}
