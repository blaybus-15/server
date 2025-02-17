package com.blaybus.server.domain.senior;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CareLevel {
    NO_GRADE("등급없음"),
    LEVEL_1("1등급"),
    LEVEL_2("2등급"),
    LEVEL_3("3등급"),
    LEVEL_4("4등급"),
    LEVEL_5("5등급"),
    COGNITIVE_SUPPORT("인지지원 등급");

    private final String description;

    public static CareLevel fromString(String text) {
        for (CareLevel level : CareLevel.values()) {
            if (level.description.equalsIgnoreCase(text)) {
                return level;
            }
        }
        return NO_GRADE;  // 기본값
    }
}
