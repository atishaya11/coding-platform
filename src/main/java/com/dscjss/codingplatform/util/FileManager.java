package com.dscjss.codingplatform.util;

import com.amazonaws.AmazonClientException;
import com.dscjss.codingplatform.problems.exception.TestDataDownloadException;
import com.dscjss.codingplatform.s3.S3Services;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

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

    public File downloadTestDataFile(String fileName) throws InterruptedException {
        String path = System.getProperty("java.io.tmpdir") + "/sample-test/" + fileName;
        File dir = new File(path);
        boolean created = dir.mkdirs();
        File tempFile = new File(dir.getAbsolutePath() +"/" + System.currentTimeMillis());
        s3Services.downloadFile(fileName, tempFile.getAbsolutePath());
        return tempFile;
    }


    public void deleteTestDataFiles(String[] files){
        for(String file : files){
            s3Services.deleteFile(file);
        }
    }
}