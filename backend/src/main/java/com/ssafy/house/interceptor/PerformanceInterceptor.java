package com.ssafy.house.interceptor;

import java.util.Arrays;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PerformanceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        log.debug("##요청 경로: {}, 요청 방식: {}##", request.getRequestURI(), request.getMethod());
        log.debug("요청 파라미터 분석");
        request.getParameterMap().forEach((k, v) -> {
            log.debug(" - name: {}, value: {}", k, Arrays.toString(v));
        });

        long start = System.currentTimeMillis();
        request.setAttribute("start", start);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        long middle = System.currentTimeMillis();
        long start = (Long) request.getAttribute("start");
        request.setAttribute("middle", middle);
        log.debug("요청 URL: {}, handler 처리 시간: {}", request.getRequestURL(), middle - start);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex)
            throws Exception {
        long end = System.currentTimeMillis();
        long start = (Long) request.getAttribute("start");
        log.debug("요청 URL: {}, 최종 처리 시간: {}", request.getRequestURL(), end - start);

    }
}
