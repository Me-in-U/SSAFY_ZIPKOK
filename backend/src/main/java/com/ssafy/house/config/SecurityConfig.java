package com.ssafy.house.config;

import com.ssafy.house.Security.JwtAuthenticationFilter;
import com.ssafy.house.Security.JwtUtil;
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

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
            UserDetailsService uds) {
        return new JwtAuthenticationFilter(jwtUtil, uds);
    }

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

                        // unauthenticated users can login / register
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/members/login",
                                "/api/v1/members")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/house/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/sidogungu/**")
                        .permitAll()

                        // allow your JSP views
                        .requestMatchers(HttpMethod.GET,
                                "/", "/index",
                                "/member/mvLogin", "/member/mvRegist")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/house/**")
                        .permitAll()

                        // allow AI endpoints
                        .requestMatchers(HttpMethod.POST,
                                "/ai/advised",
                                "/ai/tool",
                                "/ai/member")
                        .permitAll()

                        // static resources & swagger
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**")
                        .permitAll()
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
     * Global CORS configuration source, so that @CrossOrigin or
     * the above .cors() call actually picks up these origins & methods.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://api.ssafy.blog",
                "https://ssafy.blog",
                "http://api.ssafy.blog",
                "http://192.168.204.108:5173",
                "http://172.22.16.1:5173"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // apply to all paths
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
