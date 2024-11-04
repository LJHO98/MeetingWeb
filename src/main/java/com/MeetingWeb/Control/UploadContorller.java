package com.MeetingWeb.Control;

import com.MeetingWeb.Service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UploadContorller {
    private final FileStorageService fileStorageService;

    @PostMapping("/img")
    public ResponseEntity<Map<String, String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("error", "File is empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String fileUrl = fileStorageService.storeFile(file);
            response.put("location", fileUrl); // TinyMCE가 요구하는 key "location"
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "File upload failed");
            return ResponseEntity.status(500).body(response);
        }
    }
}
