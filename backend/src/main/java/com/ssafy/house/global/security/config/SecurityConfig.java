package com.ssafy.house.global.security.config;

import com.ssafy.house.global.security.filter.JwtAuthenticationFilter;
import com.ssafy.house.global.security.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * JWT 기반 보안 정책과 CORS 정책을 설정한다.
 */
@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 해시 인코더를 생성한다.
     *
     * @return 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT 인증 필터를 생성한다.
     *
     * @param jwtUtil JWT 유틸리티
     * @param uds 사용자 상세 서비스
     * @return JWT 인증 필터
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
            UserDetailsService uds) {
        return new JwtAuthenticationFilter(jwtUtil, uds);
    }

    /**
     * 애플리케이션의 보안 필터 체인을 구성한다.
     *
     * @param http Spring Security HTTP 설정 객체
     * @param jwtFilter JWT 인증 필터
     * @return 보안 필터 체인
     * @throws Exception 필터 체인 구성 중 예외가 발생한 경우
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtFilter)
            throws Exception {

        http
                // disable CSRF; we're stateless
                .csrf(csrf -> csrf.disable())
                // use JWT, no sessions
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // enable CORS support
                .cors(Customizer.withDefaults())
                // authorize requests
                .authorizeHttpRequests(auth -> auth
                        // allow all preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 유저 관리(로그인/회원가입)
                        .requestMatchers(HttpMethod.POST,
                                "/v1/auth/login",
                                "/v1/auth/register")
                        .permitAll()
                        // 계정 확인
                        .requestMatchers(HttpMethod.GET,
                                "/v1/member/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.PUT,
                                "/v1/member/me")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/v1/member/me/favorites/*")
                        .authenticated()
                        .requestMatchers(HttpMethod.DELETE,
                                "/v1/member/**",
                                "/v1/member/me/favorites/*")
                        .authenticated()
                        // 아파트 정보 조회
                        .requestMatchers(HttpMethod.GET,
                                "/v1/house/**",
                                "/v1/region/**",
                                "/v1/recommend/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/v1/house/**")
                        .permitAll()
                        // 뉴스 티커 로딩
                        .requestMatchers(HttpMethod.GET,
                                "/v1/news/**")
                        .permitAll()
                        // AI 채팅
                        .requestMatchers(HttpMethod.POST,
                                "/ai/house",
                                "/ai/user-controlled",
                                "/v1/ai/house",
                                "/v1/ai/user-controlled",
                                "/api/v1/ai/house",
                                "/api/v1/ai/user-controlled")
                        .permitAll()
                        // static resources & swagger
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**")
                        .permitAll()
                        //게시판
                        .requestMatchers(HttpMethod.GET, "/v1/community/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/community/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/community/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/community/**").authenticated()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // everything under /api must be authenticated
                        .requestMatchers("/api/**").authenticated()
                        // everyone else is allowed (e.g. error pages)
                        .anyRequest().permitAll())
                // plug in your JWT filter before username/password filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // disable form login & http basic
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * 전역 CORS 설정을 생성한다.
     *
     * @return CORS 설정 소스
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://ssafy.ios.kr",
                "http://ssafy.ios.kr",
                "http://192.168.204.108:5173",
                "http://172.22.16.1:5173",
                "http://localhost:8080"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // apply to all paths
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
