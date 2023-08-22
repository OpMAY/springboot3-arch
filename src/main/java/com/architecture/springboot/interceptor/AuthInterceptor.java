package com.architecture.springboot.interceptor;

import com.architecture.springboot.util.Security;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

@Log4j2
@Component
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final Environment environment;
    private final Security security;
    private boolean isApiTokenValidationOn; // 디버그 요소 -> 개발 단계 OFF, 배포 단계 ON

    @PostConstruct
    public void AuthInterceptorInitialize() {
        isApiTokenValidationOn = Boolean.getBoolean(environment.getProperty("server.settings.api-jwt"));
        log.info(
                "AuthInterceptorInitialized, isApiTokenValidationOn : {}",
                isApiTokenValidationOn
        );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (checkMethodJWTRequired(handlerMethod, method) && isApiTokenValidationOn) {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION)
                    .substring(request.getHeader(HttpHeaders.AUTHORIZATION)
                            .lastIndexOf("bearer ") + 7);
            log.info("validation : {}", token);
            return security.validateToken(token);
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private boolean checkMethodJWTRequired(HandlerMethod handlerMethod, Method method) {
        return (method.isAnnotationPresent(JWTRequired.class) || handlerMethod.getBeanType().isAnnotationPresent(JWTRequired.class))
                && (!method.isAnnotationPresent(JWTExcepted.class) && !handlerMethod.getBeanType().isAnnotationPresent(JWTExcepted.class));
    }
}
