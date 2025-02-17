package com.blaybus.server.domain;

import com.blaybus.server.dto.request.CareGiverRequest;
import com.blaybus.server.dto.request.MyPageRequest.MemberUpdateRequest;
import com.blaybus.server.dto.request.SignUpRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareGiver extends Member {

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    @Column(name = "contact_number")
    private String contactNumber; // 연락처

    @Column(name = "certificate_number")
    private String certificateNumber; // 자격증 번호

    @Column(name = "certificate_type")
    @Enumerated(EnumType.STRING)
    private CareGiverType careGiverType; // 요양보호사, 사회복지사 등

    @Column(name = "has_vehicle")
    private boolean hasVehicle; // 차량 소유 여부

    @Column(name = "completed_dementia_training")
    private boolean completedDementiaTraining; // 치매교육 이수 여부

    @Column(name = "address")
    private String address; // 주소

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "caregiver_kinds", joinColumns = @JoinColumn(name = "caregiver_id"))
    @Column(name = "kind")
    private List<String> kind; // 선생님을 나타낼 수 있는 단어 목록

    @Column(name = "experience")
    private String experience; // 현재까지의 총 경력 ("X년 Y개월")

    @Column(name = "is_working")
    private boolean isWorking; // 근무 중 여부

    @Column(name = "work_start_date")
    private LocalDate workStartDate; // 현재 근무 시작 날짜

    @Column(name = "major_experience")
    private String majorExperience; // 주요 경력

    @Column(name = "introduction")
    private String introduction; // 한줄 소개

    @Column(name = "profile_picture_url")
    private String profilePictureUrl; // 프로필 사진

    @Column(name = "hour_pay")
    private int hourPay;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank")
    private BankType bank;
    private String account;
    private String accountName;

    public CareGiver(String email, String password,
                     LoginType loginType, GenderType genderType, String name) {
        super(email, password, Set.of(MemberRole.CAREGIVER), loginType, name);
        this.genderType = genderType;
    }

    public void updateCareGiverInfo(CareGiverRequest request) {
        this.certificateNumber = request.getContactNumber();
        this.careGiverType = request.getCareGiverType();
        this.address = request.getAddress();
        this.hasVehicle = request.isHasVehicle();
        this.completedDementiaTraining = request.isCompletedDementiaTraining();
        this.contactNumber = request.getContactNumber();

        this.kind = request.getKind();
        this.experience = getCareerDuration(request.getCertificatedAt(), request.getEndCertificatedAt());
        this.majorExperience = request.getMajorExperience();
        this.introduction = request.getIntroduction();
        this.hourPay = request.getHourPay();
        this.bank = request.getBank();
        this.account = request.getAccount();
        this.accountName = request.getAccountName();
    }

    public void updateCareGiverInfo(MemberUpdateRequest request) {
        this.contactNumber = request.getContactNumber();
        this.certificateNumber = request.getCertificateNumber();
        this.careGiverType = request.getCareGiverType();
        this.hasVehicle = request.isHasVehicle();
        this.completedDementiaTraining = request.isCompletedDementiaTraining();
        this.address = request.getAddress();
        this.introduction = request.getIntroduction();
        this.kind = request.getKind();
        this.profilePictureUrl = request.getProfilePictureUrl();
        this.bank = request.getBank();
        this.account = request.getAccount();
        this.accountName = request.getAccountName();
    }

    public void updateCareGiverInfo(CareGiverRequest request, String profilePictureUrl) {
        this.certificateNumber = request.getContactNumber();
        this.careGiverType = request.getCareGiverType();
        this.address = request.getAddress();
        this.hasVehicle = request.isHasVehicle();
        this.completedDementiaTraining = request.isCompletedDementiaTraining();
        this.contactNumber = request.getContactNumber();
        this.kind = request.getKind();
        this.experience = getCareerDuration(request.getCertificatedAt(), request.getEndCertificatedAt());
        this.majorExperience = request.getMajorExperience();
        this.introduction = request.getIntroduction();
        this.hourPay = request.getHourPay();
        this.bank = request.getBank();
        this.account = request.getAccount();
        this.accountName = request.getAccountName();
        // 추가: 전달받은 profilePictureUrl로 프로필 사진 업데이트
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * 근무 시작 시 호출하는 메서드
     */
    public void startWork() {
        if (!isWorking) { // 근무 중이 아닐 때만 시작 가능
            this.isWorking = true;
            this.workStartDate = LocalDate.now();
        }
    }

    /**
     * 근무 종료 시 호출하는 메서드
     */
    public void endWork() {
        if (isWorking && workStartDate != null) {
            LocalDate today = LocalDate.now();
            Period workPeriod = Period.between(workStartDate, today);

            // 기존 경력 기간을 가져와서 합산
            this.experience = updateExperience(workPeriod);

            // 근무 종료 처리
            this.isWorking = false;
            this.workStartDate = null;
        }
    }

    /**
     * 기존 경력 + 새로운 근무 기간을 합산하여 업데이트하는 메서드
     */
    private String updateExperience(Period workPeriod) {
        int totalYears = workPeriod.getYears();
        int totalMonths = workPeriod.getMonths();

        // 기존 경력 기간을 가져오기
        if (this.experience != null && !this.experience.isEmpty()) {
            String[] parts = this.experience.split("년 |개월");
            if (parts.length == 2) {
                totalYears += Integer.parseInt(parts[0].trim());
                totalMonths += Integer.parseInt(parts[1].trim());
            }
        }

        // 개월 수가 12개월이 넘으면 1년 추가
        if (totalMonths >= 12) {
            totalYears += totalMonths / 12;
            totalMonths = totalMonths % 12;
        }

        return totalYears + "년 " + totalMonths + "개월";
    }

    /**
     *
     * @return X년 Y개월 로 변환
     */
    public String getCareerDuration(LocalDate startDate, LocalDate endDate) {
        if (startDate != null) {
            Period period = Period.between(startDate, endDate);

            int years = period.getYears();
            int months = period.getMonths();

            this.experience = (years + "년 " + months + "개월");
        }
        return null;
    }
}