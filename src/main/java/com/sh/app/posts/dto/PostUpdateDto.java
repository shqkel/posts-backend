package com.sh.app.posts.dto;

import com.sh.app.posts.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostUpdateDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    /**
     * 기존 Post객체를 인자로 받아 새 입력값 부분만 갱신한다.
     * @param post
     * @return
     */
    public Post toPost(Post post) {
        post.setTitle(title);
        post.setContent(content);
        return post;
    }
}
