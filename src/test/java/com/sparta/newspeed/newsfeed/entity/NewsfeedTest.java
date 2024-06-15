package com.sparta.newspeed.newsfeed.entity;

import com.sparta.newspeed.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedTest {
    User user = User.builder()
            .userSeq(1L)
            .userId("asdf1234")
            .userPassword("asdf1234")
            .userName("홍길동")
            .userEmail("email@email.com")
            .role(UserRoleEnum.USER)
            .build();

    @Test
    @DisplayName("게시글 생성")
    public void createNewsFeedTest() {
        NewsfeedRequestDto request= new NewsfeedRequestDto("제목", "내용", "Netflix", 3);
        Ott ott = new Ott(request.getOttName(), 40000, request.getRemainMember());
        Newsfeed newsfeed = Newsfeed.builder()
                .newsFeedSeq(1L)
                .title(request.getTitle())
                .content(request.getContent())
                .remainMember(request.getRemainMember())
                .user(user)
                .ott(ott)
                .like(0L)
                .build();
        Long seq = 1L;
        assertEquals(seq, newsfeed.getNewsFeedSeq());
        assertEquals(request.getTitle(), newsfeed.getTitle());
        assertEquals(request.getContent(), newsfeed.getContent());
        assertEquals(request.getRemainMember(), newsfeed.getRemainMember());
        assertEquals(user, newsfeed.getUser());
        assertEquals(ott, newsfeed.getOtt());
    }
    @Test
    @DisplayName("게시글 수정")
    public void updateNewsFeedTest() {
        NewsfeedRequestDto request = new NewsfeedRequestDto("제목1수정됨", "내용1수정됨", "Watcha", 1);
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
        Ott updatedOtt = new Ott(request.getOttName(), 40000, request.getRemainMember());
        newsfeed.updateNewsfeed(request, updatedOtt);
        assertEquals(request.getTitle(), newsfeed.getTitle());
        assertEquals(request.getContent(), newsfeed.getContent());
        assertEquals(request.getRemainMember(), newsfeed.getRemainMember());
        assertEquals(updatedOtt, newsfeed.getOtt());
    }
}