package com.dscjss.codingplatform.s3;


import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import static com.amazonaws.services.s3.internal.Constants.MB;

@Configuration
public class AmazonS3Config {


    private Logger logger = LoggerFactory.getLogger(AmazonS3Config.class);

    @Value("${amazonProperties.endPointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("${amazonProperties.testCasesBucketName}")
    private String testCasesBucketName;

    @Value("${amazonProperties.region}")
    private String region;


    public String getTestCasesBucketName() {
        return testCasesBucketName;
    }

    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        return s3Client;
    }

    @Bean
    public TransferManager transferManager() {

        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client())
                .withDisableParallelDownloads(false)
                .withMinimumUploadPartSize((long) (5 * MB))
                .withMultipartUploadThreshold((long) (16 * MB))
                .withMultipartCopyPartSize((long) (5 * MB))
                .withMultipartCopyThreshold((long) (100 * MB))
                .withExecutorFactory(() -> createExecutorService(20))
                .build();

        int oneDay = 1000 * 60 * 60 * 24;
        Date oneDayAgo = new Date(System.currentTimeMillis() - oneDay);

        try {

            tm.abortMultipartUploads(testCasesBucketName, oneDayAgo);

        } catch (AmazonClientException e) {
            logger.error("Unable to upload file, upload was aborted, reason: " + e.getMessage());
        }

        return tm;
    }

    private ThreadPoolExecutor createExecutorService(int threadNumber) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int threadCount = 1;

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("jsa-amazon-s3-transfer-manager-worker-" + threadCount++);
                return thread;
            }
        };
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber, threadFactory);
    }
}