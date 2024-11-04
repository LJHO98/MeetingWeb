package com.MeetingWeb.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    @Value("${groupImgPath}")
    private String groupImgPath;

    @Value("${groupUploadPath}")
    private String groupUploadPath;

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(groupImgPath, fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/" + fileName;
    }
}
