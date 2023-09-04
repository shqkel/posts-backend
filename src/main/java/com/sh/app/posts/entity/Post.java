package com.sh.app.posts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "SEQ_POST_ID", initialValue = 1, allocationSize = 1)
public class Post {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "SEQ_POST_ID")
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    private String writer;

    @Column(nullable = false, length = 4000)
    private String content;

//    @Column(columnDefinition = "date default sysdate") // 생략시, 컬럼타입 timestamp 지정
    @CreationTimestamp
    private LocalDateTime createdAt;

}
