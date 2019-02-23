package com.dscjss.codingplatform.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class S3ServicesImpl implements S3Services {

    private Logger logger = LoggerFactory.getLogger(S3ServicesImpl.class);

    private final TransferManager transferManager;

    private final AmazonS3Config amazonS3Config;

    @Autowired
    public S3ServicesImpl(TransferManager transferManager, AmazonS3Config amazonS3Config) {
        this.transferManager = transferManager;
        this.amazonS3Config = amazonS3Config;
    }

    @Override
    public void uploadFile(String fileName, File file) throws AmazonClientException, InterruptedException{
        final PutObjectRequest request = new PutObjectRequest(amazonS3Config.getTestCasesBucketName(), fileName, file);

        Upload upload = transferManager.upload(request);

        upload.waitForCompletion();
    }

    @Override
    public void downloadFile(String keyName, String downloadFilePath) throws AmazonClientException, InterruptedException {
        final GetObjectRequest request = new GetObjectRequest(amazonS3Config.getTestCasesBucketName(), keyName);

        /*request.setGeneralProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                String transferredBytes = "Downloaded bytes: " + progressEvent.getBytesTransferred();
                logger.info(transferredBytes);
            }
        });
*/
        Download download = transferManager.download(request, new File(downloadFilePath));

        download.waitForCompletion();
    }

    @Override
    public void deleteFile(String keyName) {
        try {
            amazonS3Config.s3Client().deleteObject(new DeleteObjectRequest(amazonS3Config.getTestCasesBucketName(), keyName));
        } catch(SdkClientException e) {
            e.printStackTrace();
        }

    }
}
