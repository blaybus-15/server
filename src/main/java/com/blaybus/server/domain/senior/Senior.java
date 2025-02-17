package com.blaybus.server.domain.senior;

import com.blaybus.server.common.exception.CareLinkException;
import com.blaybus.server.common.exception.ErrorCode;
import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.Day;
import com.blaybus.server.domain.GenderType;
import com.blaybus.server.dto.request.SeniorRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Senior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false, unique = true, length = 13)
    private String serialNumber; // 랜덤 일련번호

    @Column(nullable = false)
    private LocalDate birthDate; // 생년월일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderType genderType; // 성별

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CareLevel careLevel; // 장기요양등급

    @Column(nullable = false)
    private String address; // 주소

    @Column(nullable = false)
    private String contactInfo; // 어르신 연락처

    @Column(nullable = false)
    private String guardianContact; // 보호자 연락처

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitType visitType; // 방문 유형 (정기/단기)

    // 정기 방문일 경우 사용 (요일 선택)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Day> visitDays = new HashSet<>();

    // 단기 방문일 경우 사용 (일자 선택)
    @ElementCollection
    private Set<LocalDate> visitDates = new HashSet<>();

    @Column(nullable = false)
    private LocalTime careStartTime; // 케어 시작 시간

    @Column(nullable = false)
    private LocalTime careEndTime; // 케어 종료 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MobilityLevel mobilityLevel; // 거동 가능 여부 (스스로 가능, 부축 필요, 거동 불가)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EatingLevel eatingLevel; // 식사 가능 여부 (스스로 가능, 식사 도움 필요, 콧줄/벤트 사용)

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<MedicalCondition> medicalConditions = new HashSet<>(); // 질환 정보 (다중 선택 가능)

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<RequiredService> requiredServices = new HashSet<>(); // 필요 서비스 목록

    private String additionalNotes; // 기타 사항

    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private Center center; // 시니어가 속한 센터

    @PrePersist
    @PreUpdate
    private void validateData() {
        // 방문 유형 유효성 검사
        if (visitType == null) {
            throw new CareLinkException(ErrorCode.INVALID_VISIT_TYPE);
        }

        // 방문 유형별 처리
        if (visitType == VisitType.REGULAR) {
            visitDates.clear(); // 정기 방문이면 특정 날짜 방문 데이터 초기화
            if (visitDays.isEmpty()) {
                throw new CareLinkException(ErrorCode.MISSING_REGULAR_VISIT_DAYS);
            }
        } else if (visitType == VisitType.TEMPORARY) {
            visitDays.clear(); // 단기 방문이면 요일 데이터 초기화
            if (visitDates.isEmpty()) {
                throw new CareLinkException(ErrorCode.MISSING_TEMPORARY_VISIT_DATES);
            }
        }

        // 질환 정보 유효성 검사
        if (medicalConditions.contains(MedicalCondition.NONE)) {
            medicalConditions.clear(); // "해당 없음"이면 목록을 비움
        } else if (medicalConditions.size() != new HashSet<>(medicalConditions).size()) {
            throw new CareLinkException(ErrorCode.INVALID_MEDICAL_CONDITION);
        }
    }

    public void updateSeniorPartial(SeniorRequest request) {
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getBirthDate() != null) {
            this.birthDate = request.getBirthDate();
        }
        if (request.getGenderType() != null) {
            this.genderType = request.getGenderType();
        }
        if (request.getCareLevel() != null) {
            this.careLevel = request.getCareLevel();
        }
        if (request.getAddress() != null) {
            this.address = request.getAddress();
        }
        if (request.getContactInfo() != null) {
            this.contactInfo = request.getContactInfo();
        }
        if (request.getGuardianContact() != null) {
            this.guardianContact = request.getGuardianContact();
        }
        if (request.getVisitType() != null) {
            this.visitType = request.getVisitType();
        }
        if (request.getVisitDays() != null) {
            this.visitDays = request.getVisitDays();
        }
        if (request.getVisitDates() != null) {
            this.visitDates = request.getVisitDates();
        }
        if (request.getCareStartTime() != null) {
            this.careStartTime = request.getCareStartTime();
        }
        if (request.getCareEndTime() != null) {
            this.careEndTime = request.getCareEndTime();
        }
        if (request.getMobilityLevel() != null) {
            this.mobilityLevel = request.getMobilityLevel();
        }
        if (request.getEatingLevel() != null) {
            this.eatingLevel = request.getEatingLevel();
        }
        if (request.getMedicalConditions() != null) {
            this.medicalConditions = request.getMedicalConditions();
        }
        if (request.getRequiredServices() != null) {
            this.requiredServices = request.getRequiredServices();
        }
        if (request.getAdditionalNotes() != null) {
            this.additionalNotes = request.getAdditionalNotes();
        }
    }


}
