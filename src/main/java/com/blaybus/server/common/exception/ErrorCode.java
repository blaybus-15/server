package com.blaybus.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 500 Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CareLink Server Error"),

    // 400 Bad Request
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "비밀번호가 다릅니다."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "유저 타입이 잘못되었습니다."),
    INVALID_CERTIFICATE_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 자격증 종류입니다."),
    INVALID_CERTIFICATE_NUMBER_CAREWORKER(HttpStatus.BAD_REQUEST,"요양보호사 자격증 번호는 9자리 숫자 + 대문자 1자리 형식이어야 합니다."),
    INVALID_CERTIFICATE_NUMBER_SOCIALWORKER(HttpStatus.BAD_REQUEST,"사회복지사 자격증 번호는 '1-123456' 또는 '2-123456' 형식이어야 합니다."),
    INVALID_CERTIFICATE_NUMBER_NURSINGASSISTANT(HttpStatus.BAD_REQUEST, "간호조무사 자격증 번호는 '1-123456' 또는 '2-123456' 형식이어야 합니다."),
    INVALID_VISIT_TYPE(HttpStatus.BAD_REQUEST, "올바른 방문 유형(정기/단기)이 설정되어야 합니다."),
    MISSING_REGULAR_VISIT_DAYS(HttpStatus.BAD_REQUEST, "정기 방문을 선택한 경우 방문 요일을 하나 이상 지정해야 합니다."),
    MISSING_TEMPORARY_VISIT_DATES(HttpStatus.BAD_REQUEST, "단기 방문을 선택한 경우 방문 날짜를 하나 이상 지정해야 합니다."),
    INVALID_SALARY_TYPE(HttpStatus.BAD_REQUEST, "올바른 급여 유형(시급/일급)이 설정되어야 합니다."),
    INVALID_SALARY_AMOUNT(HttpStatus.BAD_REQUEST, "급여는 0보다 커야 합니다."),
    INVALID_MEDICAL_CONDITION(HttpStatus.BAD_REQUEST, "유효하지 않은 질환 정보입니다."),
    NOT_ADMIN(HttpStatus.BAD_REQUEST, "해당 회원은 어드민이 아닙니다."),
    INVALID_ADMIN_IDENTIFIER(HttpStatus.BAD_REQUEST, "유효하지 않은 admin 식별자입니다."),
    INVALID_CENTER_NAME(HttpStatus.BAD_REQUEST, "센터 이름이 적절하지 않습니다."),
    INVALID_RESPONSE_STATUS_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 응답 타입입니다."),


    NO_ADDRESS(HttpStatus.BAD_REQUEST, "주소에 동(洞) 정보가 포함되지 않았습니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 유저입니다."),
    CENTER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 센터입니다."),
    SENIOR_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 어르신입니다."),
    MATCHING_NOT_FOUND(HttpStatus.NOT_FOUND, "매칭이 존재하지 않습니다."),

    // 409 Conflict,
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "중복된 유저입니다.");

    private final HttpStatus status;
    private final String msg;

    ErrorCode(HttpStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
