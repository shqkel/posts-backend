package com.sh.app.auth.dto;

import com.sh.app.auth.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class OAuthUserDto {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;

    public Member toMember() {
        return Member.builder()
                .memberId(id)
                .password("1234") // 임의 비밀번호 not null
                .name(name)
                .email(email)
                .build();
    }
}
