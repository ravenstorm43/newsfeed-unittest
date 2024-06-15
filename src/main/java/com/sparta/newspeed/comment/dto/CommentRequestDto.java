package com.sparta.newspeed.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    @Schema(description = "댓글 내용")
    @NotBlank(message = "빈칸에 댓글을 작성하세요.")
    private String content;
}