package com.sparta.newspeed.newsfeed.service;

import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.entity.Ott;
import com.sparta.newspeed.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.newsfeed.repository.OttRepository;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsfeedServiceTest {
    @Mock
    NewsfeedRespository newsfeedRespository;
    @Mock
    OttRepository ottRepository;
    @Test
    void getNewsfeeds() {

    }

    @Test
    @DisplayName("게시글 상세조회")
    void getNewsfeedTest() {
        Long newsFeedSeq = 1L;
        Long userSeq = 4L;
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(newsFeedSeq)
                .title("제목1")
                .content("내용1")
                .remainMember(3)
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
        given(newsfeedRespository.findById(newsFeedSeq)).willReturn(Optional.of(newsfeed));
        given(newsfeedRespository.findById(newsFeedSeq)).willThrow(new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);

        // when
        NewsfeedResponseDto response = newsfeedService.getNewsfeed(newsFeedSeq);

        // then
        assertEquals(newsfeed.getNewsFeedSeq(), response.getNewsFeedSeq());
        assertEquals(newsfeed.getTitle(), response.getTitle());
        assertEquals(newsfeed.getContent(), response.getContent());
        assertEquals(newsfeed.getRemainMember(), response.getRemainMember());
        assertEquals(newsfeed.getUser().getUserName(), response.getUserName());
        assertEquals(newsfeed.getOtt().getOttName(), response.getOttName());
    }

    @Test
    @DisplayName("게시글 상세조회시 게시글을 찾을 수 없을 때")
    void getNewsfeedExceptionTest1() {
        Long newsFeedSeq = 1L;
        Long userSeq = 4L;
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(newsFeedSeq)
                .title("제목1")
                .content("내용1")
                .remainMember(3)
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
        given(newsfeedRespository.findById(newsFeedSeq)).willThrow(new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeedService.getNewsfeed(newsFeedSeq));
        assertEquals(ErrorCode.NEWSFEED_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("게시글 생성")
    void createNewsFeedTest() {
        // given
        Long newsFeedSeq = 1L;
        Long userSeq = 4L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1", "내용1", "Netflix", 3);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        given(ottRepository.findByOttName(request.getOttName())).willReturn(Optional.of(ott));
        when(newsfeedRespository.save(any(Newsfeed.class))).then(AdditionalAnswers.returnsFirstArg()); // any(Newsfeed.class): Newsfeed클래스에 속한 엔티티 아무거나 파라미터로 받음, AdditionalAnswers.returnsFirstArg(): 첫 번째 파라미터를 리턴
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);

        // when
        NewsfeedResponseDto response = newsfeedService.createNewsFeed(request, user);

        // then
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getContent(), response.getContent());
        assertEquals(request.getRemainMember(), response.getRemainMember());
        assertEquals(user.getUserName(), response.getUserName());
        assertEquals(request.getOttName(), response.getOttName());
    }
    @Test
    @DisplayName("게시글 생성시 남은 멤버 수가 최대 멤버수를 초과할 때")
    void createNewsFeedExceptionTest1() {
        // given
        Long userSeq = 4L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1", "내용1", "Netflix", 5);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        given(ottRepository.findByOttName(request.getOttName())).willReturn(Optional.of(ott));
//        when(newsfeedRespository.save(any(Newsfeed.class))).then(AdditionalAnswers.returnsFirstArg()); // any(Newsfeed.class): Newsfeed클래스에 속한 엔티티 아무거나 파라미터로 받음, AdditionalAnswers.returnsFirstArg(): 첫 번째 파라미터를 리턴
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeedService.createNewsFeed(request, user));
        assertEquals(ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER, exception.getErrorCode());
    }
    @Test
    @DisplayName("게시글 생성시 ott 플랫폼을 찾을 수 없을 때")
    void createNewsFeedExceptionTest2() {
        // given
        Long userSeq = 4L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1", "내용1", "Net", 3);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        given(ottRepository.findByOttName(request.getOttName())).willThrow(new CustomException(ErrorCode.OTT_NOT_FOUND));
//        when(newsfeedRespository.save(any(Newsfeed.class))).then(AdditionalAnswers.returnsFirstArg()); // any(Newsfeed.class): Newsfeed클래스에 속한 엔티티 아무거나 파라미터로 받음, AdditionalAnswers.returnsFirstArg(): 첫 번째 파라미터를 리턴
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeedService.createNewsFeed(request, user));
        assertEquals(ErrorCode.OTT_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("게시글 수정")
    void updateNewsFeedTest() {
        // given
        Long userSeq = 4L;
        Long newsfeedSeq = 1L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1수정됨", "내용1수정됨", "Netflix", 1);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(newsfeedSeq)
                .title("제목1")
                .content("내용1")
                .remainMember(3)
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
        given(newsfeedRespository.findByNewsFeedSeqAndUser(newsfeedSeq, user)).willReturn(Optional.of(newsfeed));
        given(ottRepository.findByOttName(request.getOttName())).willReturn(Optional.of(ott));
        given(newsfeedRespository.existsById(newsfeedSeq)).willReturn(true);
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);
        // when
        NewsfeedResponseDto response = newsfeedService.updateNewsFeed(newsfeedSeq, request, user);

        // then
        assertEquals(newsfeedSeq, response.getNewsFeedSeq());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getContent(), response.getContent());
        assertEquals(request.getRemainMember(), response.getRemainMember());
        assertEquals(user.getUserName(), response.getUserName());
        assertEquals(request.getOttName(), response.getOttName());
    }
    @Test
    @DisplayName("게시글 수정시 남은 멤버 수가 최대 멤버수를 초과할 때")
    void updateNewsFeedExceptionTest1() {
        // given
        Long userSeq = 4L;
        Long newsfeedSeq = 1L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1수정됨", "내용1수정됨", "Netflix", 6);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(newsfeedSeq)
                .title("제목1")
                .content("내용1")
                .remainMember(3)
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
//        given(newsfeedRespository.findByNewsFeedSeqAndUser(newsfeedSeq, user)).willReturn(Optional.of(newsfeed));
        given(ottRepository.findByOttName(request.getOttName())).willReturn(Optional.of(ott));
//        given(newsfeedRespository.existsById(newsfeedSeq)).willReturn(true);
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeedService.createNewsFeed(request, user));
        assertEquals(ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER, exception.getErrorCode());
    }
    @Test
    @DisplayName("게시글 수정 시 게시글을 찾을 수 없을 때")
    void updateNewsFeedExceptionTest2() {
        // given
        Long userSeq = 4L;
        Long newsfeedSeq = 2L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1수정됨", "내용1수정됨", "Netflix", 1);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(1L)
                .title("제목1")
                .content("내용1")
                .remainMember(3)
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
        given(newsfeedRespository.existsById(newsfeedSeq)).willReturn(false);
//        given(newsfeedRespository.findByNewsFeedSeqAndUser(newsfeedSeq, user)).willReturn(Optional.of(newsfeed));
//        given(ottRepository.findByOttName(request.getOttName())).willReturn(Optional.of(ott));
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);
        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeedService.updateNewsFeed(newsfeedSeq, request, user));
        assertEquals(ErrorCode.NEWSFEED_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("게시글 수정 시 작성자 본인이 아닐 때")
    void updateNewsFeedExceptionTest3() {
        // given
        Long userSeq = 4L;
        Long newsfeedSeq = 1L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1수정됨", "내용1수정됨", "Netflix", 1);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(1L)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(newsfeedSeq)
                .title("제목1")
                .content("내용1")
                .remainMember(3)
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
        given(newsfeedRespository.existsById(newsfeedSeq)).willReturn(true);
        given(newsfeedRespository.findByNewsFeedSeqAndUser(newsfeedSeq, user)).willThrow(new CustomException(ErrorCode.NEWSFEED_NOT_USER));
//        given(ottRepository.findByOttName(request.getOttName())).willReturn(Optional.of(ott));
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);
        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeedService.updateNewsFeed(newsfeedSeq, request, user));
        assertEquals(ErrorCode.NEWSFEED_NOT_USER, exception.getErrorCode());
    }
    @Test
    @DisplayName("게시글 수정 시 ott 플랫폼을 찾을 수 없을 때")
    void updateNewsFeedExceptionTest4() {
        // given
        Long userSeq = 4L;
        Long newsfeedSeq = 1L;
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1수정됨", "내용1수정됨", "Net", 1);
        Ott ott = new Ott(request.getOttName(), 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .photoName("스프링르탄이.png")
                .role(UserRoleEnum.USER)
                .build();
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(newsfeedSeq)
                .title("제목1")
                .content("내용1")
                .remainMember(3)
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
        given(newsfeedRespository.findByNewsFeedSeqAndUser(newsfeedSeq, user)).willReturn(Optional.of(newsfeed));
        given(newsfeedRespository.existsById(newsfeedSeq)).willReturn(true);
        given(ottRepository.findByOttName(request.getOttName())).willThrow(new CustomException(ErrorCode.OTT_NOT_FOUND));
        NewsfeedService newsfeedService = new NewsfeedService(newsfeedRespository, ottRepository);
        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeedService.updateNewsFeed(newsfeedSeq, request, user));
        assertEquals(ErrorCode.OTT_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    void deleteNewsFeed() {
    }

    @Test
    void increaseNewsfeedLike() {
    }

    @Test
    void decreaseNewsfeedLike() {
    }

    @Test
    void findNewsfeed() {
    }

    @Test
    void validateNewsfeedLike() {
    }
}