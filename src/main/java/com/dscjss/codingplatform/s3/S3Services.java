package com.dscjss.codingplatform.s3;


import com.amazonaws.AmazonClientException;

import java.io.File;

public interface S3Services {

    void uploadFile(String fileName, File file) throws AmazonClientException, InterruptedException ;

    void downloadFile(String keyName, String downloadFilePath) throws InterruptedException;

    void deleteFile(String keyName);
}
