package com.sh.app.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.app.auth.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * BasicAuthenticationFilter은 Authentication Basic 인증을 처리하는 필터. 
 * - 커스텀 인증/인가 처리가 필요하다면 상속해서 구현한다. 
 * 
 * OncePerRequestFilter
 * - 요청당 한번만 실행되는 필터(servlet간 dispatch에는 사용되지 않는다.)
 *
 */
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;

	public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	/**
	 * AuthorizationFilter는 Bearer토큰 검증과 인증객체 생성을 담당하는 필터이다.
	 * - 모든 요청에 AuthorizationFilter이 실행된다.
	 * - 그중 Bearer JWT 토큰이 있는 경우만, JWT 토큰을 파싱, Authentication객체를 SecurityContextHolder에 저장한다.
	 * - UsernamePasswordAuthenticationFilter전에 인증처리를 담당한다.
	 *
	 * - postmat 테스트중 회원가입하는데 유효하지 않은 Bearer토큰이 전달되면, 오류가 날수 있다.
	 *
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String Authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		log.debug("{} Authorization:{}", request.getRequestURI(), Authorization);

		// Authorization Bearer Token이 정상적으로 입력된 경우, SecurityContextHolder에 인증정보를 보관
		if(Authorization != null && Authorization.startsWith("Bearer ")) {
			try {
				// 1. token 유효성검사 및 parsing
				Map<String, Object> claims = jwtTokenProvider.decodeAccessToken(Authorization.replace("Bearer ", ""));
				String username = (String) claims.get("username");
				List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) claims.get("authorities");
				log.debug("username / authroties from claim = {} / {}", username, authorities); // honggd / [ROLE_USER]

				// Token이 만료된 경우, TokenExpiredException(JWTVerificationException의 자식클래스)이 던져진다.
				// com.auth0.jwt.exceptions.TokenExpiredException: The Token has expired on Wed Feb 09 23:33:17 KST 2022.

				// 2. 인증객체 생성
				// 사용자 권한 조회 : username claim이 존재하는 경우
				if(username != null) {
					UsernamePasswordAuthenticationToken authenticationToken = 
							new UsernamePasswordAuthenticationToken(username, null, authorities); // 비밀번호 검증 필요없음.
					
				    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				    log.debug("Authentication from SecurityContextHolder : {}", SecurityContextHolder.getContext().getAuthentication());
				}
			} catch (IllegalArgumentException | JWTVerificationException e) {
				log.error("Access Token 인증 오류! ", e);
				// 응답 메세지 처리 
				// 1. header
//				response.setHeader("error", e.getMessage());
//				response.sendError(HttpStatus.FORBIDDEN.value());
				
				// 2. body
				response.setStatus(HttpStatus.FORBIDDEN.value());
				Map<String, Object> error = new HashMap<>();
				error.put("error_message", e.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE); // utf-8 명시하지 않아도 major browser는 정상적으로 처리한다.
				new ObjectMapper().writeValue(response.getOutputStream(), error);

				return;
				
			}

		}
		
		chain.doFilter(request, response);
		
	}
	

}
