package com.sh.app.oauth.service;

import com.sh.app.auth.entity.Member;
import com.sh.app.auth.entity.MemberDetails;
import com.sh.app.auth.jwt.JwtTokenProvider;
import com.sh.app.auth.service.AuthService;
import com.sh.app.oauth.dto.NaverProfileResponseDto;
import com.sh.app.oauth.dto.NaverTokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverService {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    private String NAVER_CLIENT_ID = "4FZPjXcAfMhSgAAJoUSi";
    private String NAVER_CLIENT_SECRET = "_Ogd1oi2Es";
    private String NAVER_TOKEN_URI = "https://nid.naver.com/oauth2.0/token";
    private String NAVER_PROFILE_URI = "https://openapi.naver.com/v1/nid/me";

    public Map<String, Object> callback(String code, HttpServletRequest request) {
        log.debug("code = {}", code);
        // token 요청
        String uri = NAVER_TOKEN_URI
                   + "?grant_type=authorization_code"
                   + "&client_id=" + NAVER_CLIENT_ID
                   + "&client_secret=" + NAVER_CLIENT_SECRET
                   + "&code=" + code;
        RestTemplate restTemplate = new RestTemplate();
        NaverTokenResponseDto tokenResponse = restTemplate.getForObject(uri, NaverTokenResponseDto.class);
        log.debug("tokenResponse = {}", tokenResponse); // {access_token=AAAAOqlqKPbDMVETLmuzOAYr4KNCtae-6lHQ2iN6rXzBUHe8zS6qdZ1RFzLY2CYkV0fmYHZ3XHUB4iePjBtgVFDORNc, refresh_token=4jONVpTnDBeGJ91OPp0O4rg3amiiipIeGipWB2mtzuQtTXS7dJulpUSipGyGDoqdGTHCsk7cHnP9y3xcZHB7ULjjL5dwrispJd5oMPgiickIeO6L8Ris5OhPsF47CdOFbpmmxII, token_type=bearer, expires_in=3600}

        // 사용자 정보 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getAccessToken()); // Authorization : Bearer XXXX 설정
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<NaverProfileResponseDto> profileResponseEntity = restTemplate.exchange(NAVER_PROFILE_URI, HttpMethod.GET, requestEntity, NaverProfileResponseDto.class);
        log.debug("profileResponseEntity = {}", profileResponseEntity); // <200,{resultcode=00, message=success, response={id=E-CgI_mW4E10TWy5ZXYfi3Ua19yhJnAvf_SyMrS8Dgg, nickname=찐똥구리, profile_image=https://phinf.pstatic.net/contact/profile/blog/36/79/dongxuan09.jpg, age=40-49, gender=M, email=dongxuan1863@gmail.com, mobile=010-4002-1863, mobile_e164=+821040021863, name=김동현, birthday=02-05, birthyear=1982}},[Server:"nginx", Date:"Thu, 24 Aug 2023 03:44:26 GMT", Content-Type:"application/json; charset=utf-8", Content-Length:"416", Connection:"keep-alive", Keep-Alive:"timeout=5", Vary:"Accept-Encoding", apigw-uuid:"537de823-b275-4bad-9c2b-401aed6ff582", Cache-Control:"no-cache, must-revalidate"]>
        NaverProfileResponseDto profileResponse = profileResponseEntity.getBody();

        // 회원가입
        String memberId = profileResponse.getNaverProfile().getId() + "@naver";
        MemberDetails memberDetails = null;
		try {
			memberDetails = (MemberDetails) authService.loadUserByUsername(memberId);
		} catch (UsernameNotFoundException e) {
			/**
			 * 존재하지 않는 경우 회원가입처리
			 * authorities 테이블에도 insert처리해야 한다.
			 */
			Member member = profileResponse.getNaverProfile().toMember();
			memberDetails = authService.createMember(member);
		}
		log.debug("memberDetails = {}", memberDetails);

		// jwt 토큰 발급
		Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, memberDetails.getPassword(), memberDetails.getAuthorities());
        return jwtTokenProvider.generateTokens(authentication, request);

    }
}
