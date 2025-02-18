package com.blaybus.server.dto.request;

import com.blaybus.server.domain.auth.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public record MyPageRequest() {
    @Data
    @Builder
    @AllArgsConstructor
    public static final class MemberUpdateRequest {
        // 이름
        private String name;
        // 생년월일
        private int year;
        private int month;
        private int day;
        // 연락처
        private String contactNumber;
        // 성별
        private GenderType genderType;
        // 차량 소유 여부
        private boolean hasVehicle;
        // 자격증 이수 여부
        private List<CareGiverCertificate> certificates;
        // 치매 교육 이수 여부
        private boolean completedDementiaTraining;
        //
        private String streetAddress; // 도로명 주소 (예: 서울특별시 성동구 금호동4가 123)
        private String detailAddress; // 상세 주소 (예: 101호)
        // 한줄소개
        private String introduction;
        // 성격
        private List<Kindness> kinds;
        // 경력사항 수정
        private List<Experience> experiences;
        // 계좌관리
        private BankType bank;
        private String account;
        private String accountName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static final class AdminUpdateRequest {
        Long centerId;
        private String name;
        private String contactNumber; // 연락처
        private String introduction; // 한줄 소개
    }
}
