package com.architecture.springboot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@Component
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class RestInterceptor implements HandlerInterceptor {
    private final Environment environment;
    private String accessKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isApiTokenValidationOn = Boolean.getBoolean(environment.getProperty("server.settings.api-jwt"));
        accessKey = environment.getProperty("server.settings.accesskey");
        if (isApiTokenValidationOn) {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION)
                    .substring(request.getHeader(HttpHeaders.AUTHORIZATION)
                            .lastIndexOf("bearer ") + 7);
            return true;
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
}
