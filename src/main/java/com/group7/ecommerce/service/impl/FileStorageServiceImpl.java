package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Path UPLOAD_DIR = Path.of("temp");
    private static final Path STATIC_IMAGES_DIR = Path.of("src/main/resources/static/images");
    @Value("${app.valid-image-extensions}")
    private String validImageExtensions;

    @Override
    public Path unzipImages(MultipartFile zipFile) {
        if (zipFile.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn file ZIP ảnh.");
        }
        try {
            // Tạo thư mục nếu chưa có
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }

            // Giải nén
            try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path filePath = UPLOAD_DIR.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        Files.createDirectories(filePath);
                    } else {
                        Files.createDirectories(filePath.getParent());
                        Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zis.closeEntry();
                }
            }
            return UPLOAD_DIR;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi giải nén file ZIP", e);
        }
    }

    @Override
    public String copyImageToStatic(String sourcePath, String imageName) throws IOException {
        String newFileName = generateUniqueFileName(imageName);
        Path targetFile = STATIC_IMAGES_DIR.resolve(newFileName);

        if (!Files.exists(targetFile.getParent())) {
            Files.createDirectories(targetFile.getParent());
        }

        Files.copy(Paths.get(sourcePath), targetFile, StandardCopyOption.REPLACE_EXISTING);
        return "/images/" + newFileName;
    }

    @Override
    public String copyImageToStatic(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.trim().isEmpty()) {
            originalName = "unnamed.dat";
        } else {
            originalName = StringUtils.cleanPath(originalName);
        }

        String newFileName = generateUniqueFileName(originalName);
        Path targetFile = STATIC_IMAGES_DIR.resolve(newFileName);

        if (!Files.exists(targetFile.getParent())) {
            Files.createDirectories(targetFile.getParent());
        }

        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        return "/images/" + newFileName;
    }


    @Override
    public void deleteImage(String imageUrl) throws IOException {
        Path path = STATIC_IMAGES_DIR.resolve(Paths.get(imageUrl).getFileName());
        Files.deleteIfExists(path);
    }

    @Override
    public void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }

    @Override
    public boolean isValidImageFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return false;
        String lower = fileName.toLowerCase();
        List<String> validExtensions = List.of(validImageExtensions.split(","));
        return validExtensions.stream().anyMatch(lower::endsWith);
    }

    private String generateUniqueFileName(String originalName) {
        String extension = Optional.ofNullable(originalName)
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf('.')))
                .orElse("");
        return UUID.randomUUID() + extension;
    }
}
