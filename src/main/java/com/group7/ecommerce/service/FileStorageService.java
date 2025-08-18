package com.group7.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    Path unzipImages(MultipartFile zipFile);
    String copyImageToStatic(String sourcePath, String imageName) throws IOException;
    void deleteDirectoryRecursively(Path path) throws IOException;
    String copyImageToStatic(MultipartFile file) throws IOException;
    void deleteImage(String imageUrl) throws IOException;
    boolean isValidImageFile(String fileName);
}
