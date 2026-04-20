package com.example.tttn.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    public String storeProductImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String originalFileName = imageFile.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Ten file anh khong hop le.");
        }

        String sanitizedFileName = Paths.get(originalFileName).getFileName().toString();
        if (sanitizedFileName.isBlank()) {
            throw new IllegalArgumentException("Ten file anh khong hop le.");
        }

        try {
            byte[] imageBytes = imageFile.getBytes();
            saveImageToDirectory(Paths.get("src/main/resources/static/image"), sanitizedFileName, imageBytes);
            saveImageToDirectory(Paths.get("target/classes/static/image"), sanitizedFileName, imageBytes);
            return "/image/" + sanitizedFileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Khong the luu file anh.", ex);
        }
    }

    private void saveImageToDirectory(Path baseDir, String fileName, byte[] imageBytes) throws IOException {
        Path uploadDir = baseDir.toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        Path destinationFile = uploadDir.resolve(fileName).normalize();
        if (!destinationFile.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Duong dan file anh khong hop le.");
        }

        Files.write(destinationFile, imageBytes);
    }
}
