package com.blaybus.server.domain.auth;

import com.blaybus.server.domain.journal.CareJournal;
import com.blaybus.server.dto.request.CareGiverRequest;
import com.blaybus.server.dto.request.MyPageRequest.MemberUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareGiver extends Member {

    @Column(name = "name", nullable = false)
    private String name; // 이름

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    @Column(name = "contact_number")
    private String contactNumber; // 연락처

    @Column(name = "certificate_number")
    private String certificateNumber; // 자격증 번호

    @OneToMany(mappedBy = "careGiver", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CareGiverCertificate> certificates;

    @Column(name = "completed_dementia_training")
    private boolean completedDementiaTraining; // 치매교육 이수 여부

    @Column(name = "street_address")
    private String streetAddress; // 도로명 주소 (예: 서울특별시 성동구 금호동4가 123)
    @Column(name = "detail_address")
    private String detailAddress; // 상세 주소 (예: 101호)
    @Column(name = "region")
    private String region; // 행정구역 검색을 위한 필드 (예: 서울특별시 종로구)

    @Column(name = "has_vehicle")
    private boolean hasVehicle; // 차량 소유 여부

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "caregiver_kinds", joinColumns = @JoinColumn(name = "caregiver_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "kinds")
    private List<Kindness> kinds; // 선생님을 나타낼 수 있는 단어 목록

    @OneToMany(mappedBy = "careGiver", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(name = "experiences")
    private List<Experience> experiences; // 현재까지의 총 경력 ("X년 Y개월")
    @Column(name = "career_period")
    private String careerPeriod;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "caregiver_matching_conditions", joinColumns = @JoinColumn(name = "caregiver_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_conditions")
    private List<MatchingConditionType> selectedConditions;

    @Column(name = "is_working")
    private boolean isWorking; // 근무 중 여부
    @Column(name = "work_start_date")
    private LocalDate workStartDate; // 현재 근무 시작 날짜

    // 효주 추가
    @OneToMany(mappedBy = "careGiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CareJournal> careJournals;
    // 효주 추가 끝

    private int birthYear;
    private int birthMonth;
    private int birthDay;

    public CareGiver(String email, String password,
                     LoginType loginType, GenderType genderType, String name, String profilePictureUrl) {
        super(email, password, Set.of(MemberRole.CAREGIVER), loginType);
        this.name = name;
        this.genderType = genderType;
        this.profilePictureUrl = profilePictureUrl;
    }

    public void saveBySocial(GenderType genderType, String name, String contactNumber, String profilePictureUrl) {
        this.genderType = genderType;
        this.name = name;
        this.contactNumber = contactNumber;
        this.profilePictureUrl = profilePictureUrl;
    }

    public void updateCareGiverInfo(CareGiverRequest request, List<Experience> experiences) {
        this.certificateNumber = request.getCertificateNumber();
        this.certificates.addAll(
                request.getCertificates().stream()
                        .map(cert -> new CareGiverCertificate(this, cert.getCareGiverType(), cert.getCertificatedNumber()))
                        .collect(Collectors.toList())
        );

        this.completedDementiaTraining = request.isCompletedDementiaTraining();

        this.streetAddress = request.getStreetAddress();
        this.detailAddress = request.getDetailAddress();
        this.region = extractRegionFromStreetAddress(request.getStreetAddress()); // 이건 내가 가공

        this.hasVehicle = request.isHasVehicle();

        this.kinds.addAll(request.getKinds());

        this.experiences.clear();
        this.experiences.addAll(experiences);
        this.careerPeriod = getCareerDuration(this.experiences); // 이건 내가 가공

        this.selectedConditions.addAll(request.getSelectedConditions());

        this.introduction = request.getIntroduction();

        this.hourPay = request.getHourPay();
        this.bank = request.getBank();
        this.account = request.getAccount();
        this.accountName = request.getAccountName();

        this.isWorking = false;
        this.workStartDate = null;
    }

    public void updateCareGiverInfo(MemberUpdateRequest request, List<Experience> experiences, String profilePictureUrl) {
        this.name = request.getName();

        this.birthYear = request.getYear();
        this.birthMonth = request.getMonth();
        this.birthDay = request.getDay();

        this.contactNumber = request.getContactNumber();

        this.genderType = request.getGenderType();

        this.hasVehicle = request.isHasVehicle();

        this.certificates.addAll(
                request.getCertificates().stream()
                        .map(cert -> new CareGiverCertificate(this, cert.getCareGiverType(), cert.getCertificatedNumber()))
                        .collect(Collectors.toList())
        );

        this.completedDementiaTraining = request.isCompletedDementiaTraining();

        this.streetAddress = request.getStreetAddress();
        this.detailAddress = request.getDetailAddress();
        this.region = extractRegionFromStreetAddress(request.getStreetAddress()); // 이건 내가 가공

        this.hasVehicle = request.isHasVehicle();

        this.kinds.addAll(request.getKinds());

        this.experiences.clear();
        this.experiences.addAll(experiences);
        this.careerPeriod = getCareerDuration(this.experiences); // 이건 내가 가공

        this.introduction = request.getIntroduction();

        this.bank = request.getBank();
        this.account = request.getAccount();
        this.accountName = request.getAccountName();

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
            this.careerPeriod = updateExperience(workPeriod);

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
        if (this.careerPeriod != null && !this.careerPeriod.isEmpty()) {
            String[] parts = this.careerPeriod.split("년 |개월");
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
     * 전체 경력을 합산하여 "X년 Y개월" 형식으로 반환
     * @param experiences 근무 경력 리스트
     * @return 전체 경력 기간 (예: "5년 3개월")
     */
    private String getCareerDuration(List<Experience> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return "0년 0개월";
        }

        LocalDate earliestStartDate = null;
        LocalDate latestEndDate = null;

        for (Experience exp : experiences) {
            if (earliestStartDate == null || exp.getCertificatedAt().isBefore(earliestStartDate)) {
                earliestStartDate = exp.getCertificatedAt();
            }
            if (latestEndDate == null || (exp.getEndCertificatedAt() != null && exp.getEndCertificatedAt().isAfter(latestEndDate))) {
                latestEndDate = exp.getEndCertificatedAt();
            }
        }

        if (earliestStartDate == null || latestEndDate == null) {
            return "0년 0개월"; // 종료일이 없으면 0년 0개월 처리
        }

        Period period = Period.between(earliestStartDate, latestEndDate);
        return period.getYears() + "년 " + period.getMonths() + "개월";
    }


    /**
     * 주소 조회 편의성을 위한 필드 생성
     * @param streetAddress
     * @return
     */
    private String extractRegionFromStreetAddress(String streetAddress) {
        if (streetAddress == null || streetAddress.isBlank()) {
            return null;
        }

        String[] addressParts = streetAddress.split(" ");
        if (addressParts.length < 2) {
            return null; // 주소 형식이 잘못된 경우 null 반환
        }

        // 첫 번째 단어가 "서울특별시", "경기도", "부산광역시" 등의 형태인지 확인
        String region = addressParts[0];

        if (addressParts.length > 2) {
            // 두 번째 또는 세 번째 단어가 "구", "시", "군"으로 끝나는 경우 추가
            if (addressParts[1].endsWith("시") || addressParts[1].endsWith("군") || addressParts[1].endsWith("구")) {
                region += " " + addressParts[1];
            } else if (addressParts.length > 3 && (addressParts[2].endsWith("구") || addressParts[2].endsWith("군"))) {
                region += " " + addressParts[2];
            }
        }

        return region;
    }

}