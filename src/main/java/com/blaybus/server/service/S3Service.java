package com.blaybus.server.service;

import com.blaybus.server.common.exception.CareLinkException;
import com.blaybus.server.common.exception.ErrorCode;
import com.blaybus.server.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String uploadFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(file.getContentType())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new CareLinkException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

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
