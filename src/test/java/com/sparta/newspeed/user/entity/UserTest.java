package com.sparta.newspeed.user.entity;

import com.sparta.newspeed.auth.dto.SignUpRequestDto;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    User user = User.builder()
            .userSeq(1L)
            .userId("asdf1234")
            .userPassword("asdf1234")
            .userName("홍길동")
            .userEmail("email@email.com")
            .role(UserRoleEnum.USER)
            .build();
    @Test
    @DisplayName("User Builder")
    void userBuildingTest() {
        SignUpRequestDto requestDto = new SignUpRequestDto("asdf1234", "asdf1234", "홍길동", "email@email.com");
        User newUser = User.builder()
                .userSeq(1L)
                .userId(requestDto.getUserId())
                .userPassword(requestDto.getPassword())
                .userName(requestDto.getUserName())
                .userEmail(requestDto.getEmail())
                .role(UserRoleEnum.USER)
                .build();
        Long seq = 1L;
        assertEquals(seq, user.getUserSeq());
        assertEquals("asdf1234", user.getUserId());
        assertEquals("홍길동", user.getUserName());
        assertEquals("email@email.com", user.getUserEmail());
        assertEquals(UserRoleEnum.USER, user.getRole());
    }
    @Test
    @DisplayName("User Update")
    void userUpdateTest() {
        User updatedUser = User.builder()
                .userName("김철수")
                .userIntro("자기소개1")
                .build();
        UserInfoUpdateDto updateDto = new UserInfoUpdateDto(updatedUser.getUserName(), updatedUser.getUserIntro());

        user.updateUserInfo(updateDto);

        assertEquals(updateDto.getName(), user.getUserName());
        assertEquals(updateDto.getIntro(), user.getUserIntro());
    }
    @Test
    @DisplayName("Update Password")
    void updatePasswordTest() {
        user.updatePassword("password");
        assertEquals("password", user.getUserPassword());
    }
    @Test
    @DisplayName("Update OAuth2Info")
    void updateOAuth2InfoTest() {
        user.updateOAuth2Info("김철수카카오", "profileimg");
        assertEquals("profileimg", user.getProfileImageUrl());
        assertEquals("김철수카카오", user.getUserName());
    }
    @Test
    @DisplayName("Set RefreshToken")
    void setRefreshTokenTest() {
        user.setRefreshToken("refreshtoken");
        assertEquals("refreshtoken", user.getRefreshToken());
    }
    @Test
    @DisplayName("Set PhotoName")
    void setPhotoNameTest() {
        user.setPhotoName("imgtoken");
        assertEquals("imgtoken", user.getPhotoName());
    }
}