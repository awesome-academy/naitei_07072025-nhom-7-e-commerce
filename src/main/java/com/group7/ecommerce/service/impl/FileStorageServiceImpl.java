package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path UPLOAD_DIR = Path.of("temp");

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
    public void copyImageToStatic(String sourcePath, String imageName) throws IOException {
        String normalizedImageName = imageName.replace("/", File.separator);
        Path targetFile = Paths.get("src/main/resources/static/images").resolve(normalizedImageName);

        if (!Files.exists(targetFile.getParent())) {
            Files.createDirectories(targetFile.getParent());
        }

        Files.copy(Paths.get(sourcePath), targetFile, StandardCopyOption.REPLACE_EXISTING);
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
}
