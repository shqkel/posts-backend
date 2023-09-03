package com.sh.app.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sh.app.auth.entity.Member;
import lombok.Data;

/**
 * {
 *   resultcode=00,
 *   message=success,
 *   response={
 *     id=E-CgI_mW4E10TWy5ZXYfi3Ua19yhJnAvf_SyMrS8Dgg,
 *     nickname=찐똥구리,
 *     profile_image=https://phinf.pstatic.net/contact/profile/blog/36/79/dongxuan09.jpg,
 *     age=40-49,
 *     gender=M,
 *     email=dongxuan1863@gmail.com,
 *     mobile=010-4002-1863,
 *     mobile_e164=+821040021863,
 *     name=김동현,
 *     birthday=02-05,
 *     birthyear=1982
 *   }
 * }
 */
@Data
public class NaverProfileResponseDto {

    @JsonProperty("resultcode")
    private String resultCode;
    private String message;
    @JsonProperty("response")
    private NaverProfile naverProfile;

    @Data
    public static class NaverProfile {
        private String id;
        private String name;
        private String nickname;
        @JsonProperty("profile_image")
        private String profile;
        private String gender;
        private String email;
        private String mobile;

        public Member toMember() {
            return Member.builder()
                    .memberId(id + "@naver")
                    .password("1234")
                    .name(name)
                    .email(email)
                    .build();
        }
    }

}
