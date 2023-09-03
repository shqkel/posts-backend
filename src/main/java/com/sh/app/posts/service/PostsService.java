package com.sh.app.posts.service;

import com.sh.app.posts.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostsService {
    List<Post> findAll();
    Page<Post> findAll(Pageable pageable);
    int getTotalCount();
    Post findById(Long id);
    Post save(Post post);
    void delete(Post post);
    Page<Post> findByExample(Post post, Pageable pageable);

    List<Post> findByTitleContainingOrWriterContainingOrContentContaining(String query);
}
