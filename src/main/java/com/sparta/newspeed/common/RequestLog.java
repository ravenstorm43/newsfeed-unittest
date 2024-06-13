package com.sparta.newspeed.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "request 정보")
@Aspect
@Component
public class RequestLog {
    @Pointcut("execution(* com.sparta.newspeed.*.controller..*(..))")
    private void forAllController() {}

    @After("forAllController()")
    public void showRequestLog(JoinPoint joinPoint) throws Throwable {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final String method = request.getMethod();
        final String requestUri = request.getRequestURI();

        log.info("URI : " + requestUri + " method : " + method);
    }
}
