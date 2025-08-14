package com.group7.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ProductService {

    void importFromExcelAndZip(MultipartFile excelFile, MultipartFile imageZip);
}
