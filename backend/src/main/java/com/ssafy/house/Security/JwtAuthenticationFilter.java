package com.ssafy.house.Security;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService uds;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.uds = uds;
    }

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest req,
            @SuppressWarnings("null") HttpServletResponse res,
            @SuppressWarnings("null") FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        // logger.info("=== JwtAuthFilter 시작 ===");
        // logger.info("Auth 헤더: {}", header);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = jwtUtil.validateAndGetUsername(token);
                // logger.info("토큰에서 뽑은 username: {}", username);
                UserDetails ud = uds.loadUserByUsername(username);
                // logger.info("loadUserByUsername 리턴: {}", ud.getUsername());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        ud, null, ud.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // 토큰 만료 혹은 위·변조 시 로그 또는 무시
                // logger.error("JwtAuthenticationFilter 에러: {}", e.getMessage());
            }
        }
        // logger.info("SecurityContextHolder 에 인증정보: {}",
        // SecurityContextHolder.getContext().getAuthentication());
        chain.doFilter(req, res);
    }
}
