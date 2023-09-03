package com.sh.app.oauth.service;


import com.sh.app.auth.dto.OAuthUserDto;
import com.sh.app.auth.entity.Member;
import com.sh.app.auth.entity.MemberDetails;
import com.sh.app.auth.jwt.JwtTokenProvider;
import com.sh.app.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;


    public Map<String, Object> join(OAuthUserDto dto, HttpServletRequest request) {
		log.debug("dto = {}", dto);
        String memberId = dto.getId(); // 1234567890@kakao
		MemberDetails memberDetails = null;
		try {
			memberDetails = (MemberDetails) authService.loadUserByUsername(memberId);
		} catch (UsernameNotFoundException e) {
			/**
			 * 존재하지 않는 경우 회원가입처리
			 * authorities 테이블에도 insert처리해야 한다.
			 */
			Member member = dto.toMember();
			memberDetails = authService.createMember(member);
		}
		log.debug("memberDetails = {}", memberDetails);

		// 토큰 발급
		Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, memberDetails.getPassword(), memberDetails.getAuthorities());
		return jwtTokenProvider.generateTokens(authentication, request);
    }
}
