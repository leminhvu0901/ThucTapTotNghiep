package com.example.tttn.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    //ham tra ve duong dan luu anh
    public String storeProductImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String originalFileName = imageFile.getOriginalFilename();//lay ten file goc
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Ten file anh khong hop le.");
        }

        //chi lay ten file bo duong dan phia truoc
        String sanitizedFileName = Paths.get(originalFileName).getFileName().toString();
        if (sanitizedFileName.isBlank()) {
            throw new IllegalArgumentException("Ten file anh khong hop le.");
        }

        try {
            byte[] imageBytes = imageFile.getBytes();

            Path classpathRoot = new ClassPathResource("").getFile().toPath();

            saveImageToDirectory(classpathRoot.resolve("static/image"), sanitizedFileName, imageBytes);

            Path srcImageDir = classpathRoot.getParent().getParent()
                    .resolve("src/main/resources/static/image");
            saveImageToDirectory(srcImageDir, sanitizedFileName, imageBytes);

            return "/image/" + sanitizedFileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Khong the luu file anh.", ex);
        }
    }

    //xoa anh trong thu muc
    public void deleteProductImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }
        String fileName = Paths.get(imagePath).getFileName().toString();//;lay ten anh

        try {
            Path classpathRoot = new ClassPathResource("").getFile().toPath();//target/classes
            deleteImageFromDirectory(classpathRoot.resolve("static/image"), fileName);
            deleteImageFromDirectory(classpathRoot.getParent().getParent()
                    .resolve("src/main/resources/static/image"), fileName);
        } catch (IOException ex) {
        }
    }

    //ham xoa anh 
    private void deleteImageFromDirectory(Path baseDir, String fileName) throws IOException {
        Path target = baseDir.toAbsolutePath().normalize().resolve(fileName).normalize();
        Files.deleteIfExists(target);
    }

    //ham luu anh vao thu muc
    private void saveImageToDirectory(Path baseDir, String fileName, byte[] imageBytes) throws IOException {
        Path uploadDir = baseDir.toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        Path destinationFile = uploadDir.resolve(fileName).normalize();//ghep thu muc file vào duong dan 
        if (!destinationFile.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Duong dan file anh khong hop le.");
        }

        Files.write(destinationFile, imageBytes);
    }
}
