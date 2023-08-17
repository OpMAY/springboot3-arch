package com.architecture.springboot.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class PropertyConfiguration {
    private final Environment environment;

    @PostConstruct
    public void init() {
        // TODO 필수 Properties Check
    }
}
