package com.blaybus.server.dto.request;

import com.blaybus.server.domain.journal.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CareJournalRequest {
    private Long seniorId;
    private Long careGiverId;
    private String careGiverName;
    private LocalDate seniorBirthday;
    private String seniorGender;
    private String seniorCareLevel;

    // 건강 페이지
    private Meal meal;
    private String breakfast;
    private String lunch;
    private String dinner;
    private String snack;
    private RestroomSmall restroomSmall;
    private String restroomSmallMethod;
    private String restroomSmallCondition;
    private RestroomBig restroomBig;
    private String restroomBigMethod;
    private String restroomBigCondition;

    // 활동 및 정서 페이지
    private SleepTime sleepTime;
    private SleepQuality sleepQuality;
    private String activity;
    private String emotion;

    // 위생 페이지
    private Boolean bath;
    private String skinCondition;

    // 투약 페이지
    private MedicationTime medicationTime;
    private String medicationName;

    // 특이사항 페이지
    private String specialNotes;
    private String adminNote;
}