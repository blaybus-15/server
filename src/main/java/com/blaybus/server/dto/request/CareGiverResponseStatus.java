package com.blaybus.server.dto.request;

public enum CareGiverResponseStatus {
    ACCEPT,    // 수락 → 매칭 확정 (MATCHED)
    REJECT,    // 거절 → 매칭 거절 (REJECTED)
    NEGOTIATE  // 조율 요청 → 상태 변경: NEGOTIATING (필요시 근무 조건 업데이트)
}
