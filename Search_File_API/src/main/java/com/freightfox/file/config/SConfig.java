package com.freightfox.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class SConfig {
	 
    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKeyId;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretAccessKey;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    //   fCreates and configures an AmazonS3 client bean.
    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
        // Build and return the AmazonS3 client with specified credentials
        return AmazonS3ClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public String getBucketName() {
        return bucketName;
    }


}
