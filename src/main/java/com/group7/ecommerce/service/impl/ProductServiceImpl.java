package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.request.ProductDto;
import com.group7.ecommerce.dto.request.ProductFilterDto;
import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.entity.Category;
import com.group7.ecommerce.dto.response.ProductListItemResponse;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.ProductImage;
import com.group7.ecommerce.mapper.ProductMapper;
import com.group7.ecommerce.repository.CategoryRepository;
import com.group7.ecommerce.repository.ProductImageRepository;
import com.group7.ecommerce.repository.ProductRepository;
import com.group7.ecommerce.service.FileStorageService;
import com.group7.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final Validator validator;
    private final FileStorageService fileStorageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public void importFromExcelAndZip(MultipartFile excelFile, MultipartFile imageZip) {
        try {
            // Giải nén ZIP
            Path imageDir = fileStorageService.unzipImages(imageZip);

            try (InputStream is = excelFile.getInputStream()) {
                Workbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    ProductDto dto = mapRowToDto(row);

                    // Validate DTO
                    Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errors = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.joining(", "));

                        throw new RuntimeException("Lỗi dữ liệu dòng " + (i + 1) + ": " + errors);
                    }

                    Category category = categoryRepository.findById(dto.categoryId())
                            .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + dto.categoryId()));

                    // Copy ảnh vào static và lưu URL
                    List<String> savedImageUrls = new ArrayList<>();
                    for (String img : dto.imageUrls()) {
                        if (!fileStorageService.isValidImageFile(img)) {
                            throw new RuntimeException("File không phải ảnh hợp lệ: " + img);
                        }
                        Path sourceImage = imageDir.resolve(img);
                        if (Files.exists(sourceImage)) {
                            // Dùng method chung copy và trả về URL
                            String imageUrl = fileStorageService.copyImageToStatic(sourceImage.toString(), img);
                            savedImageUrls.add(imageUrl);
                        } else {
                            throw new RuntimeException("Không tìm thấy ảnh: " + img + " cho sản phẩm ở dòng " + (i + 1));
                        }
                    }

                    // Tạo Product

                    Product product = productMapper.toEntity(dto);
                    product.setCategory(category);
                    productRepository.save(product);

                    // Thêm ảnh của sản phẩm
                    boolean first = true;
                    for (String url : savedImageUrls) {
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(product);
                        productImage.setImageUrl(url);
                        productImage.setPrimary(first);
                        first = false;
                        productImageRepository.save(productImage);
                    }
                }
            }

            // Xóa thư mục temp
            fileStorageService.deleteDirectoryRecursively(Path.of("temp"));

        } catch (Exception e) {
            throw new RuntimeException("Lỗi import sản phẩm", e);
        }
    }


    @Override
    @Transactional
    public void updateProduct(Long id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + dto.categoryId()));

        // map lại dto -> entity
        productMapper.updateEntityFromDto(product, dto);
        product.setCategory(category);

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void updateImageProduct(Long id, MultipartFile[] images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));

        List<ProductImage> oldImages = productImageRepository.findByProductId(product.getId());

        for (ProductImage img : oldImages) {
            try {
                fileStorageService.deleteImage(img.getImageUrl());
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi xóa file ảnh", e);
            }
        }
        productImageRepository.deleteAllByProductId(product.getId());

        if (images != null && images.length > 0) {
            List<ProductImage> productImages = Arrays.stream(images)
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String originalName = Optional.ofNullable(file.getOriginalFilename()).orElse("");
                        if (!fileStorageService.isValidImageFile(originalName)) {
                            throw new RuntimeException("File không phải ảnh hợp lệ: " + originalName);
                        }
                        String imageUrl;
                        try {
                            imageUrl = fileStorageService.copyImageToStatic(file);
                        } catch (IOException e) {
                            throw new RuntimeException("Lỗi khi lưu file ảnh", e);
                        }
                        ProductImage img = new ProductImage();
                        img.setImageUrl(imageUrl);
                        img.setProduct(product);

                        return img;
                    })
                    .toList();

            if (!productImages.isEmpty()) {
                productImageRepository.saveAll(productImages);
            }
        }
    }

    private ProductDto mapRowToDto(Row row) {
        String name = getCellString(row.getCell(0));
        String description = getCellString(row.getCell(1));
        BigDecimal importPrice = getBigDecimalFromCell(row.getCell(2));
        BigDecimal sellingPrice = getBigDecimalFromCell(row.getCell(3));
        int stockQuantity = getIntFromCell(row.getCell(4));
        Long categoryId = getLongFromCell(row.getCell(5));

        String imageFileName = getCellString(row.getCell(6));
        List<String> imageList = Arrays.stream(imageFileName.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        boolean isFeatured = getBooleanFromCell(row.getCell(7));
        boolean isDeleted = getBooleanFromCell(row.getCell(8));

        return new ProductDto(
                name,
                description,
                importPrice,
                sellingPrice,
                stockQuantity,
                categoryId,
                imageList,
                isFeatured,
                isDeleted
        );
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        return cell.toString().trim();
    }

    private BigDecimal getBigDecimalFromCell(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } else {
            try {
                return new BigDecimal(cell.toString().trim());
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }
    }

    private int getIntFromCell(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else {
            try {
                return Integer.parseInt(cell.toString().trim());
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private long getLongFromCell(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        } else {
            try {
                return Long.parseLong(cell.toString().trim());
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private boolean getBooleanFromCell(Cell cell) {
        if (cell == null) return false;
        if (cell.getCellType() == CellType.BOOLEAN) return cell.getBooleanCellValue();
        return Boolean.parseBoolean(cell.toString().trim());
    }

    @Override
    public Page<ProductListItemResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllActiveProducts(pageable);
    }
    
    @Override
    public Page<ProductListItemResponse> getAllProducts(ProductFilterDto filterDto, Pageable pageable) {
        // Nếu không có filter nào và sắp xếp là mặc định, sử dụng method cũ
        if (isEmptyFilter(filterDto) && isDefaultSort(filterDto)) {
            return getAllProducts(pageable);
        }
        
        // Sử dụng custom repository để filter/sort động
        return productRepository.findProductsWithFilter(filterDto, pageable);
    }
    
    /**
     * Kiểm tra xem có phải sắp xếp mặc định không
     */
    private boolean isDefaultSort(ProductFilterDto filterDto) {
        if (filterDto == null) return true;
        
        String sortBy = filterDto.sortBy();
        String sortDirection = filterDto.sortDirection();
        
        return (sortBy == null || "createdAt".equals(sortBy)) && 
               (sortDirection == null || "desc".equals(sortDirection));
    }
    
    /**
     * Kiểm tra xem filter có rỗng hay không (không tính tham số sắp xếp)
     */
    private boolean isEmptyFilter(ProductFilterDto filterDto) {
        if (filterDto == null) return true;
        
        return (filterDto.name() == null || filterDto.name().trim().isEmpty()) &&
               (filterDto.description() == null || filterDto.description().trim().isEmpty()) &&
               filterDto.minSellingPrice() == null &&
               filterDto.maxSellingPrice() == null &&
               filterDto.minImportPrice() == null &&
               filterDto.maxImportPrice() == null &&
               filterDto.minStockQuantity() == null &&
               filterDto.maxStockQuantity() == null &&
               filterDto.categoryId() == null &&
               (filterDto.categoryName() == null || filterDto.categoryName().trim().isEmpty()) &&
               filterDto.isFeatured() == null &&
               filterDto.createdAfter() == null &&
               filterDto.createdBefore() == null &&
               filterDto.updatedAfter() == null &&
               filterDto.updatedBefore() == null;
    }
}
