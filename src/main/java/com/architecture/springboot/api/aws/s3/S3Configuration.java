package com.architecture.springboot.api.aws.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author : OpMAY
 * @version : 1
 * S3 Object를 사용하기 위한 자동 Configuration (Credential Set)
 * **/
@Log4j2
@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:aws.properties")
public class S3Configuration {
    private final Environment environment;
    private final Region DEFAULT_REGION = Region.AP_NORTHEAST_2;

    @Bean
    public S3Client configure() {
        String accessKey = environment.getProperty("aws.accessKey");
        String secretKey = environment.getProperty("aws.secretKey");
        log.info("ACCESS_KEY : {}, SECRET_KEY : {}", accessKey, secretKey);
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(DEFAULT_REGION)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }


}
