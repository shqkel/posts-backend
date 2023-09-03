package com.sh.app.config;

import com.sh.app.auth.jwt.JwtTokenProvider;
import com.sh.app.filter.JwtAuthenticationFilter;
import com.sh.app.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // @Secured @PreAuthorize 사용을 위해 작성
public class WebSecurityConfig {
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        // POST 요청시 csrf token 인증 제외
//        http.csrf(CsrfConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable())
            .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.disable())
            .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션-쿠키 사용하지 않음 SessionManagementConfigurer 반환

        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
            authorizationManagerRequestMatcherRegistry
//                .requestMatchers(mvc.pattern("/auth/join"), mvc.pattern("/auth/login"), mvc.pattern("/auth/token")).permitAll()
                .requestMatchers(antMatcher("/auth/join"), antMatcher("/auth/login"), antMatcher("/auth/token")).permitAll()
                .requestMatchers(antMatcher("/oauth/**")).permitAll()
//                .requestMatchers(antMatcher("/posts/**")).permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET, "/posts/**")).permitAll() // 인증기능 추가후
                .requestMatchers(antMatcher("/actuator/**")).permitAll()
                .anyRequest().authenticated();
        });

        // jwt관련 필터추가
        http.addFilter(jwtAuthenticationFilter());
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtTokenProvider);
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager(), jwtTokenProvider);
        jwtAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth/login","POST"));
        return jwtAuthenticationFilter;
    }

}
