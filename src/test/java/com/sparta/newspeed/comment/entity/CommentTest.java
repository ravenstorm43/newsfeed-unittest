package com.sparta.newspeed.comment.entity;

import com.sparta.newspeed.comment.dto.CommentRequestDto;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.entity.Ott;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    User user = User.builder()
            .userSeq(1L)
            .userId("asdf1234")
            .userPassword("asdf1234")
            .userName("홍길동")
            .userEmail("email@email.com")
            .role(UserRoleEnum.USER)
            .build();
    Ott ott = new Ott("Netflix", 40000, 3);
    Newsfeed newsfeed = Newsfeed.builder()
            .newsFeedSeq(1L)
            .title("제목1")
            .content("내용")
            .remainMember(3)
            .user(user)
            .ott(ott)
            .like(0L)
            .build();
    @Test
    @DisplayName("댓글 작성")
    void createCommentTest() {
        CommentRequestDto requestDto = new CommentRequestDto("댓글1");
        Comment comment = Comment.builder()
                .commentSeq(1L)
                .content(requestDto.getContent())
                .user(user)
                .newsfeed(newsfeed)
                .like(0L)
                .build();
        Long seq = 1L;
        assertEquals(seq, comment.getCommentSeq());
        assertEquals(requestDto.getContent(), comment.getContent());
        assertEquals(user, comment.getUser());
        assertEquals(newsfeed, comment.getNewsfeed());
    }

    @Test
    @DisplayName("댓글 수정")
    void updateCommentTest() {
        CommentRequestDto requestDto = new CommentRequestDto("댓글1수정됨");
        Comment comment = Comment.builder()
                .commentSeq(1L)
                .content("댓글1")
                .user(user)
                .newsfeed(newsfeed)
                .like(0L)
                .build();
        comment.update(requestDto);
        assertEquals(requestDto.getContent(), comment.getContent());
    }
}