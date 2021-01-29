package com.awss3.controller;

import com.awss3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/storage")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/create-bucket/{bucketName}")
    public ResponseEntity<HttpStatus> createBucket(@PathVariable String bucketName) throws Exception {
        s3Service.createBucket(bucketName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("delete-bucket/{bucketName}")
    public ResponseEntity<HttpStatus> deleteBucket(@RequestPart String bucketName) throws Exception {
        s3Service.deleteBucket(bucketName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("upload-file")
    public String uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) throws Exception {
        return s3Service.uploadFile(multipartFile);
    }

    @DeleteMapping("delete-file")
    public ResponseEntity<HttpStatus> deleteFile(@RequestPart(value = "url") String fileUrl) throws Exception {
        s3Service.deleteFile(fileUrl);
        return ResponseEntity.ok().build();
    }

}
