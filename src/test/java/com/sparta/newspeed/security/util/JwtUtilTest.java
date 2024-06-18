package com.sparta.newspeed.security.util;

import com.sparta.newspeed.auth.controller.AuthController;
import com.sparta.newspeed.auth.dto.TokenResponseDto;
import com.sparta.newspeed.auth.service.AuthService;
import com.sparta.newspeed.mail.config.EmailConfig;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ActiveProfiles("test")
class JwtUtilTest {
    @Autowired
    JwtUtil jwtUtil;
    @BeforeEach
    void setUp() {
        jwtUtil.init();
    }

    @Test
    @DisplayName("토큰 생성")
    void createToken() {
        TokenResponseDto responseDto = jwtUtil.createToken("Kim1234567", UserRoleEnum.USER);

        assertNotNull(responseDto);
    }
}