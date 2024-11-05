package com.MeetingWeb.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileUploadService {

    @Value("${userProfileImgPath}")
    private String userProfileImgPath;

    @Value("${uploadPath}")
    private String uploadPath;

    //프로필 이미지 저장
    public String saveProfile(MultipartFile file) throws IOException {
        //원본 파일명에서 확장자 추출
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // UUID와 확장자를 결합하여 새로운 파일명 생성
        String fileName = UUID.randomUUID() + fileExtension;
        Path filePath = Paths.get(userProfileImgPath, fileName);

        // 파일 저장
        Files.write(filePath, file.getBytes());

        //URL 반환
        return uploadPath + "/" + fileName;
    }
}
