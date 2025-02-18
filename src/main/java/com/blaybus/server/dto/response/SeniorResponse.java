package com.blaybus.server.dto.response;

import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.Day;
import com.blaybus.server.domain.auth.GenderType;
import com.blaybus.server.domain.senior.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Builder
public class SeniorResponse {
    private Long id;
    private String serialNumber;
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
    private LocalTime careStartTime;
    private LocalTime careEndTime;
    private MobilityLevel mobilityLevel;
    private EatingLevel eatingLevel;
    private Set<MedicalCondition> medicalConditions;
    private Set<RequiredService> requiredServices;
    private String additionalNotes;
    private Long centerId;

    public static SeniorResponse from(Senior senior) {
        return SeniorResponse.builder()
                .id(senior.getId())
                .serialNumber(senior.getSerialNumber())
                .name(senior.getName())
                .birthDate(senior.getBirthDate())
                .genderType(senior.getGenderType())
                .careLevel(senior.getCareLevel())
                .address(senior.getAddress())
                .contactInfo(senior.getContactInfo())
                .guardianContact(senior.getGuardianContact())
                .visitType(senior.getVisitType())
                .visitDays(senior.getVisitDays())
                .visitDates(senior.getVisitDates())
                .careStartTime(senior.getCareStartTime())
                .careEndTime(senior.getCareEndTime())
                .mobilityLevel(senior.getMobilityLevel())
                .eatingLevel(senior.getEatingLevel())
                .medicalConditions(senior.getMedicalConditions())
                .requiredServices(senior.getRequiredServices())
                .additionalNotes(senior.getAdditionalNotes())
                .centerId(senior.getCenter().getId())
                .build();
    }
}
