package com.sh.app.posts.controller;

import com.sh.app.posts.dto.PostCreateDto;
import com.sh.app.posts.dto.PostUpdateDto;
import com.sh.app.posts.entity.Post;
import com.sh.app.posts.service.PostsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostsController {
    private final PostsService postsService;

    @GetMapping
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok(postsService.findAll());
    }

//    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault(size = 5, page = 0) Pageable pageable){
        Page<Post> result = postsService.findAll(pageable);
        // content, number(int 현재페이지 0-based), totalPages, totalElements(long), numberOfElement(현재 게시물수 : <= size), size, sort({empty:false, sorted:false, unsorted:true})...
        return ResponseEntity.ok(result);
    }

    /**
     * jpql 사용버젼
     * @return
     */
    @GetMapping("/totalCount")
    public ResponseEntity<?> getTotalCount() {
        return ResponseEntity.ok(postsService.getTotalCount());
    }

    @GetMapping("/search")
    public ResponseEntity<?> findByTitleContainingOrWriterContainingOrContentContaining(@RequestParam String query) {
        return ResponseEntity.ok(postsService.findByTitleContainingOrWriterContainingOrContentContaining(query));
    }
    @GetMapping("/search2")
    public ResponseEntity<?> finByExample(@RequestParam String query, @PageableDefault(value = 5, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Post post = Post.builder()
                .title(query)
                .writer(query)
                .content(query)
                .build();
        log.debug("post = {}", post);
        return ResponseEntity.ok(postsService.findByExample(post, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Post post = postsService.findById(id);
        if(post == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody @Valid PostCreateDto postCreateDto) {
        Post post = postsService.save(postCreateDto.toPost());
        return ResponseEntity.created(URI.create("/posts/" + post.getId())).build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody @Valid PostUpdateDto postUpdateDto){
        Post post = postsService.findById(id);
        if(post == null)
            return ResponseEntity.notFound().build();
        post = postUpdateDto.toPost(post);
        postsService.save(post);
        return ResponseEntity.ok(post);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){
        Post post = postsService.findById(id);
        if(post == null)
            return ResponseEntity.notFound().build();
        postsService.delete(post);
        return ResponseEntity.noContent().build();
    }
}
