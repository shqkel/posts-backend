package com.sh.app.posts.repository;

import com.sh.app.posts.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsRepository extends JpaRepository<Post, Long> {

    /**
     * custom 쿼리메소드 작성시 find이후 By를 꼭 작성한다.
     * @return
     */
    List<Post> findAllByOrderByIdDesc();
    Page<Post> findAllByOrderByIdDesc(Pageable pageable);
    @Query("select count(*) from Post")
    int getTotalCount();


    /**
     * containing 사용시에는 자동으로 앞뒤에 %로 query값을 감싸준다.
     * @param title
     * @param writer
     * @param content
     * @return
     */
    List<Post> findByTitleContainingOrWriterContainingOrContentContaining(String title, String writer, String content);
}
