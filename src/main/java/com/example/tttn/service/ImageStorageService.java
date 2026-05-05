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

            // Đường dẫn tuyệt đối đến target/classes (classpath root)
            Path classpathRoot = new ClassPathResource("").getFile().toPath();

            // Lưu vào target/classes/static/image để phục vụ ngay lập tức
            saveImageToDirectory(classpathRoot.resolve("static/image"), sanitizedFileName, imageBytes);

            // Lưu vào src/main/resources/static/image để giữ lại sau khi build lại
            Path srcImageDir = classpathRoot.getParent().getParent()
                    .resolve("src/main/resources/static/image");
            saveImageToDirectory(srcImageDir, sanitizedFileName, imageBytes);

            return "/image/" + sanitizedFileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Khong the luu file anh.", ex);
        }
    }

    public void deleteProductImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        // imagePath dạng "/image/filename.jpg" → lấy tên file
        String fileName = Paths.get(imagePath).getFileName().toString();

        try {
            Path classpathRoot = new ClassPathResource("").getFile().toPath();
            deleteImageFromDirectory(classpathRoot.resolve("static/image"), fileName);
            deleteImageFromDirectory(classpathRoot.getParent().getParent()
                    .resolve("src/main/resources/static/image"), fileName);
        } catch (IOException ex) {
            // Không throw - việc xóa ảnh thất bại không nên chặn xóa sản phẩm
        }
    }

    private void deleteImageFromDirectory(Path baseDir, String fileName) throws IOException {
        Path target = baseDir.toAbsolutePath().normalize().resolve(fileName).normalize();
        Files.deleteIfExists(target);
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
