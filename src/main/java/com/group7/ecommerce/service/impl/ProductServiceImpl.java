package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.request.ProductDto;
import com.group7.ecommerce.entity.Category;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.entity.ProductImage;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final Validator validator;
    private final FileStorageService fileStorageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

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

                    // Map dữ liệu từ Excel sang DTO
                    ProductDto dto = new ProductDto();
                    dto.setName(getCellString(row.getCell(0)));
                    dto.setDescription(getCellString(row.getCell(1)));
                    dto.setImportPrice(getBigDecimalFromCell(row.getCell(2)));
                    dto.setSellingPrice(getBigDecimalFromCell(row.getCell(3)));
                    dto.setStockQuantity(getIntFromCell(row.getCell(4)));
                    dto.setCategoryId(getLongFromCell(row.getCell(5)));

                    String imageFileName = getCellString(row.getCell(6));
                    List<String> imageList = Arrays.stream(imageFileName.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    dto.setImageUrls(imageList);

                    dto.setFeatured(getBooleanFromCell(row.getCell(7)));
                    dto.setDeleted(getBooleanFromCell(row.getCell(8)));

                    // Validate DTO
                    Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        String errors = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.joining(", "));
                        throw new RuntimeException("Lỗi dữ liệu dòng " + (i+1) + ": " + errors);
                    }

                    Category category = categoryRepository.findById(dto.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + dto.getCategoryId()));

                    // Copy ảnh vào static
                    for (String img : dto.getImageUrls()) {
                        String fileNameOnly = img.replace("\\", "/");
                        fileNameOnly = fileNameOnly.substring(fileNameOnly.lastIndexOf('/') + 1);
                        Path sourceImage = imageDir.resolve(img);
                        if (Files.exists(sourceImage)) {
                            fileStorageService.copyImageToStatic(sourceImage.toString(), fileNameOnly);
                        }
                    }

                    // Tạo Product
                    Product product = Product.builder()
                            .name(dto.getName())
                            .description(dto.getDescription())
                            .importPrice(dto.getImportPrice())
                            .sellingPrice(dto.getSellingPrice())
                            .stockQuantity(dto.getStockQuantity())
                            .category(category)
                            .isFeatured(dto.isFeatured())
                            .isDeleted(dto.isDeleted())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    productRepository.save(product);

                    // Thêm ảnh của sản phẩm
                    boolean first = true;
                    for (String img : dto.getImageUrls()) {
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(product);
                        productImage.setImageUrl(img);
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
}
