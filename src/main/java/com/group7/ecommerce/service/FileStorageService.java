package com.group7.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    Path unzipImages(MultipartFile zipFile);
    void copyImageToStatic(String sourcePath, String imageName) throws IOException;
    void deleteDirectoryRecursively(Path path) throws IOException;
}
