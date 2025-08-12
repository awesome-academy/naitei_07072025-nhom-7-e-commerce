package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.entity.Category;
import com.group7.ecommerce.entity.Product;
import com.group7.ecommerce.repository.CategoryRepository;
import com.group7.ecommerce.repository.ProductRepository;
import com.group7.ecommerce.service.FileStorageService;
import com.group7.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final FileStorageService fileStorageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

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

                    String name = getCellString(row.getCell(0));
                    String description = getCellString(row.getCell(1));
                    BigDecimal importPrice = getBigDecimalFromCell(row.getCell(2));
                    BigDecimal sellingPrice = getBigDecimalFromCell(row.getCell(3));
                    int stockQuantity = getIntFromCell(row.getCell(4));
                    long categoryId = getLongFromCell(row.getCell(5));
                    String imageFileName = getCellString(row.getCell(6));
                    boolean isFeatured = getBooleanFromCell(row.getCell(7));

                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + categoryId));

                    // Copy ảnh vào static
                    String fileNameOnly = imageFileName.replace("\\", "/");
                    fileNameOnly = fileNameOnly.substring(fileNameOnly.lastIndexOf('/') + 1);
                    Path sourceImage = imageDir.resolve(imageFileName);
                    if (Files.exists(sourceImage)) {
                        fileStorageService.copyImageToStatic(sourceImage.toString(), fileNameOnly);
                    }

                    Product product = Product.builder()
                            .name(name)
                            .description(description)
                            .importPrice(importPrice)
                            .sellingPrice(sellingPrice)
                            .stockQuantity(stockQuantity)
                            .category(category)
                            .imageUrl("images/" + fileNameOnly)
                            .isFeatured(isFeatured)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    productRepository.save(product);
                }
            }

            // Xóa temp
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
