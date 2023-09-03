package com.sh.app.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sh.app.auth.entity.MemberDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Data
@PropertySource("classpath:credentials.properties")
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

//    private long ACCESS_TOKEN_EXPIRES_IN = 1000 * 60 * 1;
//    private long REFRESH_TOKEN_EXPIRES_IN = 1000 * 60 * 60 * 24;
	private long ACCESS_TOKEN_EXPIRES_IN = 1000 * 10 * 1;
	private long REFRESH_TOKEN_EXPIRES_IN = 1000 * 30 * 1;

    public String generateAccessToken(Authentication authentication, HttpServletRequest request) {
		// algorithm - 필드에 선언하지 말것. @Value 주입타이밍이 늦어서 secret 전달불가
		Algorithm algorithm = Algorithm.HMAC512(this.secret); // HMAC Algorithm은 secret 필수

        // payload
		MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
		Collection<GrantedAuthority> _authorities = memberDetails.getAuthorities();
		List<String> authorities = _authorities.stream().map(grantedAuthority -> grantedAuthority.getAuthority().toString()).collect(Collectors.toList());

        // withClaim(String, List<?>)의 제네릭 요소는 Map, List, Boolean, Integer, Long, Double, String and Date 중 하나여야 한다.
		// java.lang.IllegalArgumentException: Expected list containing Map, List, Boolean, Integer, Long, Double, String and Date
		return JWT.create()
				.withSubject(memberDetails.getUsername()) // 주제. 다른 토큰과 구분할 수 있는 식별자역할로 StringOrURL로 작성한다.
				.withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRES_IN))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("username", memberDetails.getUsername())
				.withClaim("authorities", authorities)
				.sign(algorithm);
    }

	public String generateRefreshToken(Authentication authentication, HttpServletRequest request) {
		// algorithm - 필드에 선언하지 말것. @Value 주입타이밍이 늦어서 secret 전달불가
		Algorithm algorithm = Algorithm.HMAC512(this.secret); // HMAC Algorithm은 secret 필수
		// payload
		MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
		return JWT.create()
				.withSubject(memberDetails.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRES_IN))
				.withIssuer(request.getRequestURL().toString())
				.sign(algorithm);
	}

	public DecodedJWT decodedJWT(String jwt) {
		Algorithm algorithm = Algorithm.HMAC512(this.secret);
		JWTVerifier verifier = JWT.require(algorithm).build();
		return verifier.verify(jwt);
	}

	public Map<String, Object> decodeAccessToken(String accessToken) {
		DecodedJWT decodedJWT = decodedJWT(accessToken);

		String username = decodedJWT.getClaim("username").asString();
		String[] _authorities = decodedJWT.getClaim("authorities").asArray(String.class);
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Stream.of(_authorities).forEach(_auth -> authorities.add(new SimpleGrantedAuthority(_auth)));

		return Map.of(
				"username", username,
				"authorities", authorities);
	}
	public Map<String, Object> decodeRefreshToken(String refreshToken) {
		DecodedJWT decodedJWT = decodedJWT(refreshToken);
		String username = decodedJWT.getSubject();
		return Map.of("username", username);
	}

	public Map<String, Object> generateTokens(Authentication authentication, HttpServletRequest request) {
		return Map.of(
			"accessToken", generateAccessToken(authentication, request),
			"refreshToken", generateRefreshToken(authentication, request)
		);
	}
}
