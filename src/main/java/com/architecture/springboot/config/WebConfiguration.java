package com.architecture.springboot.config;

import com.architecture.springboot.interceptor.RestInterceptor;
import com.architecture.springboot.interceptor.WebInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@Log4j2
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
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

    @Autowired
    private RestInterceptor restInterceptor;

    @Autowired
    private WebInterceptor webInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(restInterceptorExcludedURLs);
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
