package com.iatechnology.controller;

import com.iatechnology.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String path = fileStorageService.storeFile(file, "images");
        return ResponseEntity.ok(Map.of("path", path));
    }

    @PostMapping("/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATEUR')")
    public ResponseEntity<Map<String, String>> uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        String path = fileStorageService.storeFile(file, "pdf");
        return ResponseEntity.ok(Map.of("path", path));
    }
}
