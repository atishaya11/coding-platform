package com.dscjss.codingplatform.util;

import com.amazonaws.AmazonClientException;
import com.dscjss.codingplatform.s3.AmazonS3Config;
import com.dscjss.codingplatform.s3.S3Services;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;

@Service
public class FileManager {

    private S3Services s3Services;

    @Autowired
    public FileManager(S3Services s3Services) {
        this.s3Services = s3Services;
    }

    public void uploadTestDataFile(String fileName, File file) throws FileUploadException {
        try {
            s3Services.uploadFile(fileName, file);
        } catch (InterruptedException | AmazonClientException e) {
            e.printStackTrace();
            throw new FileUploadException("Unable to upload test data file to amazon s3 " + fileName);
        }

    }

}