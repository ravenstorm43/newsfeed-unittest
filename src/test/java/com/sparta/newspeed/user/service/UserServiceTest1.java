package com.sparta.newspeed.user.service;

import com.sparta.newspeed.auth.service.AuthService;
import com.sparta.newspeed.awss3.S3Service;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.common.util.RedisUtil;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.user.dto.UserPwRequestDto;
import com.sparta.newspeed.user.dto.UserResponseDto;
import com.sparta.newspeed.user.dto.UserStatusDto;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest1 {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthService authService;
    @Mock
    RedisUtil redisUtil;
    @Mock
    S3Service s3Service;
    @Test
    @DisplayName("유저 조회")
    void getUser() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        given(s3Service.readFile(user.getPhotoName())).willReturn("https://spartanewsfeed.s3.ap-northeast-2.amazonaws.com/스프링르탄이.png");

        // when
        UserResponseDto responseDto = userService.getUser(seq);

        // then
        assertEquals(user.getUserId(), responseDto.getId());
        assertEquals(user.getUserName(), responseDto.getName());
        assertEquals(user.getUserEmail(), responseDto.getEmail());
        assertEquals("https://spartanewsfeed.s3.ap-northeast-2.amazonaws.com/" + user.getPhotoName(), responseDto.getPhotoUrl());
    }
    @Test
    @DisplayName("유저 정보 수정")
    void updateUser() throws IOException {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        UserInfoUpdateDto requestDto = new UserInfoUpdateDto("김길동","자기소개");
        MockMultipartFile file = new MockMultipartFile("image1", "image1.jpg", "image/jpg", new FileInputStream("C:/Users/admin/Downloads/image1.jpg"));
        given(s3Service.uploadFile(file)).willReturn(file.getOriginalFilename());
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when
        UserInfoUpdateDto responseDto = userService.updateUser(seq, requestDto, file);

        // then
        assertEquals(responseDto.getName(), user.getUserName());
        assertEquals(responseDto.getIntro(), user.getUserIntro());
    }
    @Test
    @DisplayName("유저 정보 사진 없이 수정")
    void updateUserWithoutFile() throws IOException {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        UserInfoUpdateDto requestDto = new UserInfoUpdateDto("김길동","자기소개");
        MockMultipartFile file = null;
        //MockMultipartFile file = new MockMultipartFile("image1", "image1.jpg", "image/jpg", new FileInputStream("C:/Users/admin/Downloads/image1.jpg"));
        //given(s3Service.uploadFile(file)).willReturn(file.getOriginalFilename());
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when
        UserInfoUpdateDto responseDto = userService.updateUser(seq, requestDto, null);

        // then
        assertEquals(responseDto.getName(), user.getUserName());
        assertEquals(responseDto.getIntro(), user.getUserIntro());
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updateUserPassword() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        UserPwRequestDto requestDto = new UserPwRequestDto("asdf1234", "qwer1234");
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getOldPassword(), user.getUserPassword())).willReturn(true);
        given(passwordEncoder.matches(requestDto.getNewPassword(), user.getUserPassword())).willReturn(false);
        given(passwordEncoder.encode(requestDto.getNewPassword())).willReturn(requestDto.getNewPassword());
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when
        userService.updateUserPassword(seq, requestDto);

        // then
        assertEquals(requestDto.getNewPassword(), user.getUserPassword());
    }

    @Test
    @DisplayName("비밀번호 변경시 기존 비밀번호와 일치하지 않을 때")
    void updateUserPasswordExceptionTest1() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        UserPwRequestDto requestDto = new UserPwRequestDto("asdf1234", "qwer1234");
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getOldPassword(), user.getUserPassword())).willReturn(false);
//        given(passwordEncoder.matches(requestDto.getNewPassword(), user.getUserPassword())).willReturn(false);
//        given(passwordEncoder.encode(requestDto.getNewPassword())).willReturn(requestDto.getNewPassword());
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateUserPassword(seq, requestDto));
        assertEquals(ErrorCode.INCORRECT_PASSWORD, exception.getErrorCode());
    }
    @Test
    @DisplayName("비밀번호 변경시 새 비밀번호가 기존과 일치할 때")
    void updateUserPasswordExceptionTest2() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        UserPwRequestDto requestDto = new UserPwRequestDto("asdf1234", "qwer1234");
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getOldPassword(), user.getUserPassword())).willReturn(true);
        given(passwordEncoder.matches(requestDto.getNewPassword(), user.getUserPassword())).willReturn(true);
//        given(passwordEncoder.encode(requestDto.getNewPassword())).willReturn(requestDto.getNewPassword());
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateUserPassword(seq, requestDto));
        assertEquals(ErrorCode.DUPLICATE_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void updateWithdraw() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        UserStatusDto requestDto = new UserStatusDto("asdf1234", "asdf1234");
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getPassword(), user.getUserPassword())).willReturn(true);
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when
        userService.updateWithdraw(seq, requestDto);

        // then
        assertEquals(UserRoleEnum.WITHDRAW, user.getRole());
    }
    @Test
    @DisplayName("회원 탈퇴시 아이디가 일치하지 않을 때")
    void updateWithdrawExceptionTest1() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        UserStatusDto requestDto = new UserStatusDto("asdf", "asdf1234");
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
//        given(passwordEncoder.matches(requestDto.getPassword(), user.getUserPassword())).willReturn(true);
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateWithdraw(seq, requestDto));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("회원 탈퇴시 비밀번호가 일치하지 않을 때")
    void updateWithdrawExceptionTest2() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        UserStatusDto requestDto = new UserStatusDto("asdf1234", "asdf");
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getPassword(), user.getUserPassword())).willReturn(false);
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateWithdraw(seq, requestDto));
        assertEquals(ErrorCode.INCORRECT_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 탈퇴시 이미 탈퇴 처리된 회원일 때")
    void updateWithdrawExceptionTest3() {
        // given
        Long seq = 4L;
        User user = User.builder()
                .userSeq(seq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.WITHDRAW)
                .build();
        UserStatusDto requestDto = new UserStatusDto("asdf1234", "asdf1234");
        given(userRepository.findById(seq)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getPassword(), user.getUserPassword())).willReturn(true);
        UserService userService = new UserService(userRepository, passwordEncoder, authService, redisUtil, s3Service);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateWithdraw(seq, requestDto));
        assertEquals(ErrorCode.USER_NOT_VALID, exception.getErrorCode());
    }

    @Test
    void mailSend() {
    }

    @Test
    void checkAuthNum() {
    }
}