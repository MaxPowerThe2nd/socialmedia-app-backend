package com.huetterprojects.social_media_app_backend;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {

    public static final String BUCKET_NAME = "social-media-app-codewiz";

    @Bean
    @ConditionalOnProperty(name = "aws.mock", havingValue = "false", matchIfMissing = true)
    public S3Client s3Client(
            @Value("${aws.s3.accessKey}") String accessKey,
            @Value("${aws.s3.secretKey}") String secretKey
    ) {
        AwsBasicCredentials credentials = AwsBasicCredentials.builder()
                .accessKeyId(accessKey)
                .secretAccessKey(secretKey)
                .build();
        return S3Client.builder()
                .credentialsProvider(() -> credentials)
                .region(Region.AP_SOUTHEAST_2)
                .build();
    }
    @Bean
    @ConditionalOnProperty(name = "aws.mock", havingValue = "true")
    public S3Client s3ClientMock() {
        return Mockito.mock(S3Client.class);
    }
}