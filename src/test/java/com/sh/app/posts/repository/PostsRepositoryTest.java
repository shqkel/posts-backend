package com.sh.app.posts.repository;

import com.sh.app.posts.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PostsRepositoryTest {

    @Autowired
    private PostsRepository postsRepository;

    @Test
    void postsREpositoryIsNotNull() {
        assertThat(postsRepository).isNotNull();
    }

    @DisplayName("게시물 등록")
    @Test
    @Transactional // query가 실행되지는 않는다.
    void insertPost() {
        Post post = Post.builder()
                .title("Why pay for ChatGPT-4")
                .writer("honggd")
                .content("Stop pay for GPT-4 when you can experience it for free with Monica - Your ChatGPT Copilot in Chrome that will boost your productivity. Give it a try RIGHT NOW!!")
                .build();
        postsRepository.save(post);
        assertThat(post.getId()).isNotNull();
        assertThat(post.getCreatedAt()).isNotNull();
    }

    @DisplayName("게시글 한건 조회")
    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void findById(Long id) {
        assertThat(id).isNotNull();

        Post post = postsRepository.findById(id).get();
        assertThat(post).isNotNull();
        assertThat(post.getId()).isEqualTo(id);

    }

    @Test
    void findAll() {
        List<Post> posts = postsRepository.findAll();
        assertThat(posts).isNotNull().isNotEmpty();
    }

    @DisplayName("게시글 수정")
    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    @Transactional
    void updatePost(Long id) {
        Post post = postsRepository.findById(id).get();
        String newTitle = post.getTitle() + "/////";
        String newContent = post.getContent() + "$$$$$";
        post.setTitle(newTitle);
        post.setContent(newContent);
        postsRepository.save(post);

        Post post2 = postsRepository.findById(id).get();
        assertThat(post2).isNotNull();
        assertThat(post2.getId()).isEqualTo(id);
        assertThat(post2.getTitle()).isEqualTo(newTitle);
        assertThat(post2.getContent()).isEqualTo(newContent);
    }

    @DisplayName("게시글 삭제")
    @ParameterizedTest
    @ValueSource(longs = {1})
    @Transactional
    void deletePost(Long id) {
        Post post = postsRepository.findById(id).get();
        postsRepository.delete(post);

        assertThatThrownBy(() -> {
            postsRepository.findById(id).orElseThrow();
        }).isExactlyInstanceOf(NoSuchElementException.class);

    }
}