package com.sh.app.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.app.auth.dto.LoginDto;
import com.sh.app.auth.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Object
 *  └ GenericFilterBean implements Filter
 * 	 └ AbstractAuthenticationProcessingFilter
 * 	  └ UsernamePasswordAuthenticationFilter
 *
 * AuthenticationManager빈을 의존주입 받는 문제가 있어 빈으로 등록하지 않고 사용.
 */
@RequiredArgsConstructor // final 필드 초기화 생성자
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;


	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		ObjectMapper objectMapper = new ObjectMapper();
		LoginDto user = null;
		try {
			user = objectMapper.readValue(request.getInputStream(), LoginDto.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String username = user.getMemberId();
		String password = user.getPassword();


		// 인증 시도
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		// UserDetailsService구현체의 loadUserByUsername을 호출하고 authentication객체를 반환. 인증실패시 null리턴
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		log.debug("인증 시도 (null이 아니면 성공): authentication = {}", authentication);
		// web service에서는 session을 사용하지 않지만 jwt토큰 발급을 위해 SecurityContextHolder에 리턴한 authentication을 저장해둔다.
		return authentication;

	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											  AuthenticationException failed) throws IOException, ServletException {

		log.debug("인증 실패 : unsuccessfulAuthentication", failed); // Bad Credential
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setCharacterEncoding("utf-8");
		response.getWriter().print("아이디 또는 비밀번호가 일치하지 않습니다.");
//		super.unsuccessfulAuthentication(request, response, failed);
	}


	/**
	 * Access Token, Refresh Token 발급하기
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
											Authentication authResult) throws IOException, ServletException {
		log.debug("인증 성공 : successfulAuthentication - authResult = {}", authResult);

		String accessToken = jwtTokenProvider.generateAccessToken(authResult, request);
		String refreshToken = jwtTokenProvider.generateRefreshToken(authResult, request);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getWriter(), tokens);
	}
}
