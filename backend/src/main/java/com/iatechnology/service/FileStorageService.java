package com.iatechnology.service;

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

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String subfolder) throws IOException {
        Path dir = Paths.get(uploadDir, subfolder);
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target);
        return subfolder + "/" + filename;
    }
}
