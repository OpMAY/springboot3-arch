package com.architecture.springboot.config;

import com.architecture.springboot.interceptor.AuthInterceptor;
import com.architecture.springboot.interceptor.WebInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final WebInterceptor webInterceptor;
    private ArrayList<String> restInterceptorExcludedURLs;
    private ArrayList<String> webInterceptorExcludedURLs;

    @PostConstruct
    public void init() {
        restInterceptorExcludedURLs = new ArrayList<>();
        webInterceptorExcludedURLs = new ArrayList<>();
        restInterceptorExcludedURLs.add("/api/auth");
//        webInterceptorExcludedURLs.add("/api/**");
        log.info("WebConfiguration Initialized");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("**")
                .order(1);
        registry.addInterceptor(webInterceptor)
                .addPathPatterns("**")
                .excludePathPatterns(webInterceptorExcludedURLs);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}
