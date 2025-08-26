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

    private static final Path TEMP_DIR = Path.of("temp");
    private final Path uploadDir;
    @Value("${app.valid-image-extensions}")
    private String validImageExtensions;

    public FileStorageServiceImpl(@Value("${app.upload-images-dir}") String uploadDirPath) throws IOException {
        this.uploadDir = Path.of(uploadDirPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    @Override
    public Path unzipImages(MultipartFile zipFile) {
        if (zipFile.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn file ZIP ảnh.");
        }
        try {
            if (!Files.exists(TEMP_DIR)) {
                Files.createDirectories(TEMP_DIR);
            }

            // Giải nén
            try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path filePath = TEMP_DIR.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        Files.createDirectories(filePath);
                    } else {
                        Files.createDirectories(filePath.getParent());
                        Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zis.closeEntry();
                }
            }
            return TEMP_DIR;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi giải nén file ZIP", e);
        }
    }

    @Override
    public String copyImageToStatic(String sourcePath, String imageName) throws IOException {
        String extension = imageName.contains(".") ? imageName.substring(imageName.lastIndexOf(".")) : "";
        String newFileName = UUID.randomUUID() + extension;

        Path targetFile = uploadDir.resolve(newFileName);
        Files.copy(Paths.get(sourcePath), targetFile, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + newFileName;
    }

    @Override
    public String copyImageToStatic(MultipartFile file) throws IOException {
        String originalName = Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed.dat");
        originalName = StringUtils.cleanPath(originalName);

        String extension = originalName.contains(".") ?
                originalName.substring(originalName.lastIndexOf(".")) : "";
        String newFileName = UUID.randomUUID() + extension;

        Path targetFile = uploadDir.resolve(newFileName);
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + newFileName;
    }

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl.startsWith("/images/")) {
            Path path = uploadDir.resolve(imageUrl.replace("/images/", ""));
            Files.deleteIfExists(path); // Xóa file thật
        }
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
}
