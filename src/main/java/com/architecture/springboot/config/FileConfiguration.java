package com.architecture.springboot.config;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.Null;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Log4j2
@Configuration
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class FileConfiguration implements WebMvcConfigurer {
    private final Environment environment;
    private long maxUploadSize;

    private long maxUploadSizePerFile;

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        try {
            String enMaxUploadSize = environment.getProperty("file.maxUploadSize");
            String enMaxUploadSizePerFile = environment.getProperty("file.maxUploadSizePerFile");
            if(enMaxUploadSize != null && enMaxUploadSizePerFile != null) {
                maxUploadSize = Long.getLong(enMaxUploadSize);
                maxUploadSizePerFile = Long.getLong(enMaxUploadSizePerFile);
            }
            MultipartConfigFactory factory = new MultipartConfigFactory();
            factory.setMaxRequestSize(DataSize.ofBytes(maxUploadSize));
            factory.setMaxFileSize(DataSize.ofBytes(maxUploadSizePerFile));

            return factory.createMultipartConfig();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
