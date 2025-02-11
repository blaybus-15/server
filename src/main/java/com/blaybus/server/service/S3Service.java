package com.blaybus.server.service;

import com.blaybus.server.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension; // 고유한 파일명 생성

        // 임시 파일 생성 후 업로드
        File tempFile = File.createTempFile("upload-", fileName);
        file.transferTo(tempFile);

        // S3 업로드 요청
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ) // 공개 URL 설정
                .build();

        s3Client.putObject(request, Paths.get(tempFile.getAbsolutePath()));

        return "https://" + s3Properties.getBucketName() + ".s3.amazonaws.com/" + fileName;
    }

    public void deleteFile(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileName)
                .build();

        s3Client.deleteObject(request);
    }
}
