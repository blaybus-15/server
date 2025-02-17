package com.blaybus.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Day {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private final String description;

    public static Day fromString(String text) {
        for (Day day : Day.values()) {
            if (day.description.equalsIgnoreCase(text)) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid day: " + text); // 예외 처리
    }
}
