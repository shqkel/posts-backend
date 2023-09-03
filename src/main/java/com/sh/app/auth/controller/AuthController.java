package com.sh.app.auth.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sh.app.auth.dto.LoginDto;
import com.sh.app.auth.entity.MemberDetails;
import com.sh.app.auth.jwt.JwtTokenProvider;
import com.sh.app.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:credentials.properties")
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        log.debug("dto = {}", dto);
        MemberDetails memberDetails = (MemberDetails) authService.loadUserByUsername(dto.getMemberId());
        log.debug("memberDetails = {}", memberDetails);
        if(memberDetails == null || !passwordEncoder.matches(dto.getPassword(), memberDetails.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("msg", "아이디 또는 비밀번호가 일치하지 않습니다."));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                memberDetails,
                memberDetails.getPassword(),
                memberDetails.getAuthorities()
        ));

        return ResponseEntity.ok(memberDetails);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> findByUsername(@PathVariable String username, @AuthenticationPrincipal String principal) {
        log.debug("@AuthenticationPrincipal user = {}", principal);
        if(!username.equals(principal))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        MemberDetails memberDetails = (MemberDetails) authService.loadUserByUsername(username);
        return ResponseEntity.ok(memberDetails);
    }

	@PostMapping("/token")
	public ResponseEntity<?> refreshAccessToken(
			@RequestBody Map<String, String> params,
			HttpServletRequest request) {
		String refreshToken = params.get("refreshToken");
		log.debug("refreshToken = {}", refreshToken);
		if(refreshToken == null)
			return ResponseEntity.badRequest().body("refreshToken is required!");

		try {
			// 검증 및 claims 가져오기
			Map<String, Object> claims = jwtTokenProvider.decodeRefreshToken(refreshToken);
			String username = (String) claims.get("username");

			// DB에서 회원정보 조회
			MemberDetails memberDetails = (MemberDetails) authService.loadUserByUsername(username);

			// 토큰 발급 (AccessToken Only)
			String accessToken = jwtTokenProvider.generateAccessToken(
					new UsernamePasswordAuthenticationToken(memberDetails, memberDetails.getPassword(), memberDetails.getAuthorities()),
					request
			);
			return ResponseEntity.ok(
						Map.of(
						"accessToken", accessToken,
						"refreshToken", refreshToken
					));
		} catch (UsernameNotFoundException | IllegalArgumentException | JWTVerificationException | JWTCreationException e) {
			log.error("Refresh AccessToken 오류!", e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error_message", e.getMessage()));
			/*
			 * 응답 body에 다음 과 같은 json message 전달!
				{
				    "error_message": "The token was expected to have 3 parts, but got 1."
				}
			 */
		}
	}





}
