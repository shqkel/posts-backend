package com.sh.app.auth.entity;


import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

/**
 * org.springframework.security.core.userdetails.User 상속
 * - 부모생성자 반드시 호출해야 한다.
 */
//@Data // Lombok needs a default constructor in the base class
@Getter
public class MemberDetails extends org.springframework.security.core.userdetails.User {
    private final Member member;
    public MemberDetails(Member member) {
        super(
            member.getMemberId(),
            member.getPassword(),
            //List<Authority> -> List<SimpleGrantedAuthority>로 변환. Collection<? extends GrantedAuthority> 타입 매개변수로 전달.
			member.getAuthorities()
				.stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuth().toString()))
				.collect(Collectors.toList())
        );
        this.member = member;
    }
}
