package com.sparta.newspeed.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class SignUpRequestDtoTest {
    @DisplayName("회원가입 테스트")
    @Nested
    class createDtoTest {
        @Test
        @DisplayName("회원가입 요청 dto 생성")
        void createUserDtoTest() {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto("Kim1234567", "Qwer1234!@#", "홍길동", "Kim1234567@email.com");

            // when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validate(signUpRequestDto);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("회원가입 요청 dto 생성 잘못된 id")
        void createUserDtoIdValidationTest() {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto("kim234567", "Qwer1234!@#", "홍길동", "Kim1234567@email.com");

            // when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validate(signUpRequestDto);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting("message")
                    .contains("아이디는 대소문자 포함 영문 + 숫자만을 허용합니다.(10 ~ 20)");
        }

        @Test
        @DisplayName("회원가입 요청 dto 생성 잘못된 비밀번호")
        void createUserDtoPasswordValidationTest() {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto("kim1234567", "Qwer1234567", "홍길동", "Kim1234567@email.com");

            // when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validate(signUpRequestDto);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting("message")
                    .contains("비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.");
        }

        @Test
        @DisplayName("회원가입 요청 dto 생성 잘못된 이메일")
        void createUserDtoEmailValidationTest() {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto("kim1234567", "Qwer1234!@#", "홍길동", "Kim1234567email.com");

            // when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validate(signUpRequestDto);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting("message")
                    .contains("must be a well-formed email address");
        }

        @Test
        @DisplayName("회원가입 요청 dto 생성 이름 미입력")
        void createUserDtoNotBlankValidationTest() {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto("kim1234567", "Qwer1234!@#", "", "Kim1234567@email.com");

            // when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validate(signUpRequestDto);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations)
                    .extracting("message")
                    .contains("must not be blank");
        }
    }
    private Set<ConstraintViolation<SignUpRequestDto>> validate(SignUpRequestDto signUpRequestDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(signUpRequestDto);
    }
}