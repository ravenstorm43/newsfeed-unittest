package com.sparta.newspeed.comment.service;

import com.sparta.newspeed.comment.dto.CommentRequestDto;
import com.sparta.newspeed.comment.dto.CommentResponseDto;
import com.sparta.newspeed.comment.entity.Comment;
import com.sparta.newspeed.comment.repository.CommentRepository;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.entity.Ott;
import com.sparta.newspeed.newsfeed.service.NewsfeedService;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    NewsfeedService newsfeedService;
    @Mock
    CommentRepository commentRepository;
    @Test
    @DisplayName("댓글 생성")
    void createCommentTest() {
        // given
        long newsfeedSeq = 1L;
        Long userSeq = 4L;
        CommentRequestDto requestDto = new CommentRequestDto("댓글1");
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
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
        given(newsfeedService.findNewsfeed(newsfeedSeq)).willReturn(newsfeed);
        when(commentRepository.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        CommentService commentService = new CommentService(newsfeedService, commentRepository);

        // when
        CommentResponseDto responseDto = commentService.createComment(newsfeedSeq, requestDto, user);

        // then
        assertEquals(user.getUserName(), responseDto.getUserName());
        assertEquals(requestDto.getContent(), responseDto.getContent());
    }
    @Test
    @DisplayName("댓글 전체 조회")
    void findAllTest() {
        // given
        Long[] commentSeq = {1L, 2L, 3L};
        long newsfeedSeq = 1L;
        Long userSeq = 4L;
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
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
        List<Comment> commentList = new ArrayList<>();
        for (int i = 0; i < commentSeq.length; i++){
            commentList.add(Comment.builder()
                    .commentSeq(commentSeq[i])
                    .content("댓글" + (i + 1))
                    .user(user)
                    .newsfeed(newsfeed)
                    .like(0L)
                    .build()
            );
        }
        given(newsfeedService.findNewsfeed(newsfeedSeq)).willReturn(newsfeed);
        given(commentRepository.findByNewsfeedNewsFeedSeq(newsfeedSeq)).willReturn(commentList);
        CommentService commentService = new CommentService(newsfeedService, commentRepository);

        // when
        List<CommentResponseDto> responseDtoList = commentService.findAll(newsfeedSeq);
        for(int i = 0; i < commentList.size(); i++) {
            assertEquals(responseDtoList.get(i).getUserName(), commentList.get(i).getUser().getUserName());
            assertEquals(responseDtoList.get(i).getContent(), commentList.get(i).getContent());
        }
    }
    @Test
    @DisplayName("댓글 조회시 댓글이 한 개도 없을 때")
    void findAllExceptionTest1() {
        // given
        Long[] commentSeq = {1L, 2L, 3L};
        long newsfeedSeq = 1L;
        Long userSeq = 4L;
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
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
        List<Comment> commentList = new ArrayList<>();
//        for (int i = 0; i < commentSeq.length; i++){
//            commentList.add(Comment.builder()
//                    .commentSeq(commentSeq[i])
//                    .content("댓글" + (i + 1))
//                    .user(user)
//                    .newsfeed(newsfeed)
//                    .like(0L)
//                    .build()
//            );
//        }
        given(newsfeedService.findNewsfeed(newsfeedSeq)).willReturn(newsfeed);
        given(commentRepository.findByNewsfeedNewsFeedSeq(newsfeedSeq)).willReturn(commentList);
        CommentService commentService = new CommentService(newsfeedService, commentRepository);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> commentService.findAll(newsfeedSeq));
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("댓글 수정")
    void updateCommentTest() {
        // given
        Long commentSeq = 2L;
        long newsfeedSeq = 1L;
        Long userSeq = 4L;
        CommentRequestDto requestDto = new CommentRequestDto("댓글1수정됨");
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
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
        Comment comment = Comment.builder()
                .commentSeq(commentSeq)
                .content("댓글1")
                .user(user)
                .newsfeed(newsfeed)
                .like(0L)
                .build();
        given(newsfeedService.findNewsfeed(newsfeedSeq)).willReturn(newsfeed);
        given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(comment));
        CommentService commentService = new CommentService(newsfeedService, commentRepository);

        // when
        CommentResponseDto responseDto = commentService.updateComment(newsfeedSeq, commentSeq, requestDto, user);

        // then
        assertEquals(comment.getUser().getUserName(), responseDto.getUserName());
        assertEquals(comment.getContent(), responseDto.getContent());
    }

    @Test
    @DisplayName("댓글 수정시 댓글이 존재하지 않을 때")
    void updateCommentExceptionTest1() {
        // given
        Long commentSeq = 2L;
        long newsfeedSeq = 1L;
        Long userSeq = 4L;
        CommentRequestDto requestDto = new CommentRequestDto("댓글1수정됨");
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
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
//        Comment comment = Comment.builder()
//                .commentSeq(commentSeq)
//                .content("댓글1")
//                .user(user)
//                .newsfeed(newsfeed)
//                .like(0L)
//                .build();
        given(newsfeedService.findNewsfeed(newsfeedSeq)).willReturn(newsfeed);
        given(commentRepository.findById(any(Long.class))).willThrow(new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        CommentService commentService = new CommentService(newsfeedService, commentRepository);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> commentService.updateComment(newsfeedSeq, commentSeq, requestDto, user));
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("댓글 수정시 사용자가 작성자 본인이 아닐 때")
    void updateCommentExceptionTest2() {
        // given
        Long commentSeq = 2L;
        long newsfeedSeq = 1L;
        Long userSeq = 4L;
        CommentRequestDto requestDto = new CommentRequestDto("댓글1수정됨");
        Ott ott = new Ott("Netflix", 40000, 4);
        User user = User.builder()
                .userSeq(userSeq)
                .userId("asdf1234")
                .userPassword("asdf1234")
                .userName("홍길동")
                .userEmail("email@email.com")
                .role(UserRoleEnum.USER)
                .build();
        User otherUser = User.builder()
                .userSeq(userSeq)
                .userId("qwer1234")
                .userPassword("qwer1234")
                .userName("김길동")
                .userEmail("email12@email.com")
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
        Comment comment = Comment.builder()
                .commentSeq(commentSeq)
                .content("댓글1")
                .user(user)
                .newsfeed(newsfeed)
                .like(0L)
                .build();
        given(newsfeedService.findNewsfeed(newsfeedSeq)).willReturn(newsfeed);
        given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(comment));
        CommentService commentService = new CommentService(newsfeedService, commentRepository);

        // when - then
        CustomException exception = assertThrows(CustomException.class, () -> commentService.updateComment(newsfeedSeq, commentSeq, requestDto, otherUser));
        assertEquals(ErrorCode.COMMENT_NOT_USER, exception.getErrorCode());
    }
    @Test
    void deleteComment() {
    }

    @Test
    void increaseCommentLike() {
    }

    @Test
    void decreaseCommentLike() {
    }

    @Test
    void validateCommentLike() {
    }
}