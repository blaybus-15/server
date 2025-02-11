package com.blaybus.server;

import com.blaybus.server.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class S3ServiceIntegrationTest {

    @Autowired
    private S3Service s3Service;

    @Test
    void testUploadAndDeleteFile() throws IOException {
        // 로컬 파일 경로 지정
        Path imagePath = Paths.get("C:\\Users\\s_ttangle1234\\Desktop\\test.png");
        byte[] fileContent = Files.readAllBytes(imagePath);

        // 로컬 파일을 MockMultipartFile로 변환
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",           // 파라미터 이름
                "test.png",       // 파일명
                "image/png",      // 콘텐츠 타입
                fileContent       // 파일 데이터
        );

        // 파일 업로드
        String fileUrl = s3Service.uploadFile(mockFile);
        assertNotNull(fileUrl);
        System.out.println("Uploaded File URL: " + fileUrl);

        // URL에서 파일명 추출 (파일명은 S3 업로드 로직에 따라 달라질 수 있음)
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

//        // 업로드한 파일 삭제
//        s3Service.deleteFile(fileName);
//        System.out.println("Deleted File: " + fileName);
    }
}
