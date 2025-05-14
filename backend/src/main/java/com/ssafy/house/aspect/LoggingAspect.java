package com.ssafy.house.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    // DAO
    @Before("execution(* com.ssafy..dao.*.*(..))")
    public void logDao(JoinPoint jp) {
        log.trace("[DAO] Method call: {}({})", jp.getSignature(), Arrays.toString(jp.getArgs()));
    }

    // Service
    @Before("execution(* com.ssafy..service.*.*(..))")
    public void logService(JoinPoint jp) {
        log.debug("[Service] Method call: {}({})", jp.getSignature(), Arrays.toString(jp.getArgs()));
    }

    // Controller
    @Before("execution(* com.ssafy..controller.*.*(..))")
    public void logController(JoinPoint jp) {
        log.info("[Controller] Method call: {}({})", jp.getSignature(), Arrays.toString(jp.getArgs()));
    }

    // Exception handler or advice (optional)
    @Before("execution(* com.ssafy..*ExceptionHandler.*(..))")
    public void logExceptionHandler(JoinPoint jp) {
        log.warn("[ExceptionHandler] Method call: {}({})", jp.getSignature(), Arrays.toString(jp.getArgs()));
    }
}
