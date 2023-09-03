package com.sh.app.posts.dto;

import com.sh.app.posts.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostCreateDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    @NotBlank(message = "작성자는 필수입니다.")
    private String writer;
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    public Post toPost() {
        return Post.builder()
                .title(title)
                .writer(writer)
                .content(content)
                .build();
    }
}
