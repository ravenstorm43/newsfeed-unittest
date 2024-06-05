package com.sparta.newspeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAIL(500, "실패했습니다."),
    USER_NOT_FOUND(400, "해당하는 유저를 찾을 수 없습니다."),
    USER_NOT_VALID(400, "이미 탈퇴 처리된 유저입니다."),
    USER_NOT_UNIQUE(400,"중복된 사용자가 존재합니다."),
    INCORRECT_PASSWORD(400, "입력하신 비밀번호가 일치하지 않습니다.")
    ;
    private int status;
    private String msg;
}