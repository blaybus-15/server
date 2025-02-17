package com.blaybus.server.dto.request;

import com.blaybus.server.domain.BankType;
import com.blaybus.server.domain.CareGiverType;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CareGiverRequest {

    @NotNull(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "자격증번호는 필수입니다")
    private String certificateNumber;
    @NotNull(message = "자격증 종류은 필수입니다.")
    private CareGiverType careGiverType;
    @NotNull(message = "주소는 필수입니다.")
    private String address;
    @NotNull(message = "차량 소유 여부는 필수입니다.")
    private boolean hasVehicle;
    @NotNull(message = "치매교육 이수 여부는 필수입니다")
    private boolean completedDementiaTraining;
    @NotNull(message = "연락처는 필수입니다.")
    @Pattern(regexp = "^010[0-9]{7,8}$", message = "연락처는 '010'으로 시작하고 뒤에 7 또는 8 자리 숫자로 작성해주세요.")
    private String contactNumber;

    private List<String> kind; // 선생님을 나타낼 수 있는 단어
    private String majorExperience; // 신입 경력
    private LocalDate certificatedAt; // 시작 경력
    private LocalDate endCertificatedAt; // 마지막 경력
    private String introduction; // 한줄 소개
    private int hourPay; // 시급
    private BankType bank; // 은행
    private String account; // 계좌번호
    private String accountName; // 예금주
}
