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
public class FileStorageService {
    @Value("${groupImgPath}")
    private String groupImgPath;

    @Value("${groupUploadPath}")
    private String groupUploadPath;


    public String storeFile(MultipartFile file) throws IOException {
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//        Path filePath = Paths.get(groupImgPath, fileName);
//
//        Files.createDirectories(filePath.getParent());
//        Files.copy(file.getInputStream(), filePath);
//
//        return "/uploads/" + fileName;
//
        // UUID와 원본 파일명 결합하여 파일 이름 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(groupImgPath, fileName);

        // 디렉토리 생성
        Files.createDirectories(filePath.getParent());
        // 파일 저장
        Files.copy(file.getInputStream(), filePath);

        // 반환할 URL 형식으로 변경
        return groupUploadPath + "/" + fileName;  // 여기서 groupUploadPath는 "/img"로 설정됨
    }

}
