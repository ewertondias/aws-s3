package com.awss3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class S3Service {

    private AmazonS3 amazonS3;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    private static final String BUCKET_NAME = "ewertonsdias-bucket-test-4";

    @PostConstruct
    private void initializeAws() {
        log.info("Initialize AWS");

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        amazonS3 = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(Regions.US_EAST_2)
            .build();
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convertFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(convertFile);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return convertFile;
    }

    private void uploadFileToS3Bucket(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public void createBucket(String bucketName) throws Exception {
        if (amazonS3.doesBucketExist(bucketName)) {
            throw new Exception("Bucket já existe");
        }

        amazonS3.createBucket(bucketName);
    }

    public void deleteBucket(String bucketName) throws Exception {
        try {
            amazonS3.deleteBucket(bucketName);
        } catch(AmazonServiceException e) {
            throw new Exception(e);
        }
    }

    public String uploadFile(MultipartFile multipartFile) throws Exception {
        String fileUrl = "";

        try {
            File file = convertMultipartToFile(multipartFile);
            String fileName = LocalDateTime.now() + ".txt";
            uploadFileToS3Bucket(fileName, file);
            file.delete();
        } catch(Exception e) {
            throw new Exception(e);
        }

        return fileUrl;
    }

    public void deleteFile(String fileUlr) throws Exception {
        try {
            String fileName = fileUlr.substring(fileUlr.lastIndexOf("/") + 1);
            amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileName));
        } catch(AmazonServiceException e) {
            throw new Exception(e);
        }
    }

}
