package com.blaybus.server.dto.request;

import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.Day;
import com.blaybus.server.domain.GenderType;
import com.blaybus.server.domain.senior.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Builder
public class SeniorRequest {
    private String name;
    private LocalDate birthDate;
    private GenderType genderType;
    private CareLevel careLevel;
    private String address;
    private String contactInfo;
    private String guardianContact;
    private VisitType visitType;
    private Set<Day> visitDays;
    private Set<LocalDate> visitDates;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime careStartTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime careEndTime;
    private MobilityLevel mobilityLevel;
    private EatingLevel eatingLevel;
    private Set<MedicalCondition> medicalConditions;
    private Set<RequiredService> requiredServices;
    private String additionalNotes;
}
