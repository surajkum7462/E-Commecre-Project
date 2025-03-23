/*
 * package com.ecom.config;
 * 
 * import org.springframework.beans.factory.annotation.Value; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration;
 * 
 * import com.amazonaws.auth.AWSStaticCredentialsProvider; import
 * com.amazonaws.auth.BasicAWSCredentials; import
 * com.amazonaws.services.s3.AmazonS3; import
 * com.amazonaws.services.s3.AmazonS3Client;
 * 
 * @Configuration public class AWSConfig {
 * 
 * @Value("${aws.access.key}") private String accessKey;
 * 
 * @Value("${aws.secret.key}") private String secretKey;
 * 
 * @Value("${aws.region}") private String region;
 * 
 * @Bean public AmazonS3 amazonS3() { BasicAWSCredentials credentials = new
 * BasicAWSCredentials(accessKey, secretKey);
 * 
 * return AmazonS3Client.builder().withRegion(region) .withCredentials(new
 * AWSStaticCredentialsProvider(credentials)).
 * 
 * build();
 * 
 * }
 * 
 * }
 */
package com.ecom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.secret.key}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}

