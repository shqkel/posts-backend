package com.sh.app.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder()
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String memberId;
    @Column(nullable = false)
    private String password;

    private String name;
    private LocalDate birthday;
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 일대다 관계에서 일(User)가 연관관계의 주인으로 부자연스러운 관계매핑
    // UserService#loadUserByUsername에서 User#authorities가 Proxy로 리턴. lazy-loading 미지원.
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "member_id")
	private final List<Authority> authorities = new ArrayList<>(); // final키워드를 사용하면 builder에서도 초기화된다.

    public void addAuthority(@NotNull Authority authority) {
        this.authorities.add(authority);
        authority.setMemberId(this.id);
    }
}
