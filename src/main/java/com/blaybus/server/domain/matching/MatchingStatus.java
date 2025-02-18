package com.blaybus.server.domain.matching;

public enum MatchingStatus {
    PROGRESS,    // 진행중
    NEGOTIATING, // 조율중
    MATCHED,     // 매칭완료
    CANCELED     // 취소
}