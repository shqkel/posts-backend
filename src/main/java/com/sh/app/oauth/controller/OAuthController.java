package com.sh.app.oauth.controller;


import com.sh.app.auth.dto.OAuthUserDto;
import com.sh.app.oauth.service.NaverService;
import com.sh.app.oauth.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/oauth")
@Slf4j
@RequiredArgsConstructor
public class OAuthController {
	private final OAuthService oauthService;
	private final NaverService naverService;

    	/**
	 * OAuth 사용자의 회원가입/로그인 핸들러
	 * @return
	 */
	@PostMapping("/join")
	public ResponseEntity<?> join(@RequestBody @Valid OAuthUserDto dto, HttpServletRequest request) {
		return ResponseEntity.ok(oauthService.join(dto, request));
	}


    /**
     * IDP가 아닌 front에서 code를 넘겨받아 회원가입/로그인처리하는 핸들러
     */
    @GetMapping("/naver/callback")
    public ResponseEntity<?> naverCallback(@RequestParam String code, HttpServletRequest request) {
        return ResponseEntity.ok(naverService.callback(code, request));
    }

}
