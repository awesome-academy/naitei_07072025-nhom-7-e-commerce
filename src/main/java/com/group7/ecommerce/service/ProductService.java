package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    void importFromExcelAndZip(MultipartFile excelFile, MultipartFile imageZip);
    void updateProduct(Long id, ProductUpdateDto dto);
    void updateImageProduct(Long id, MultipartFile[] images);

    Page<ProductListItemResponse> getAllProducts(Pageable pageable);
}
