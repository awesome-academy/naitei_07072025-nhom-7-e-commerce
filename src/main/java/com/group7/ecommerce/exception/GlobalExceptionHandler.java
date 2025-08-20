package com.group7.ecommerce.exception;

import com.group7.ecommerce.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * Xử lý lỗi validation cho @Valid annotation (Request Body)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {

        log.warn("Validation error occurred: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(getMessage("error.validation.fields"), errors));
    }

    /**
     * Xử lý lỗi validation cho @Validated (Path Variables, Request Parameters)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        log.warn("Constraint violation error: {}", ex.getMessage());

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(getMessage("error.validation.parameters"), errors));
    }

    /**
     * Xử lý lỗi type mismatch (ví dụ: truyền string cho param Integer)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        log.warn("Type mismatch error: parameter '{}' with value '{}' could not be converted to type '{}'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        String message = String.format("Tham số '%s' có giá trị '%s' không đúng định dạng",
                ex.getName(), ex.getValue());

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message));
    }

    /**
     * Xử lý lỗi thiếu required parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingParameterException(
            MissingServletRequestParameterException ex) {

        log.warn("Missing required parameter: {}", ex.getParameterName());

        String message = String.format("Thiếu tham số bắt buộc: %s", ex.getParameterName());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message));
    }

    /**
     * Xử lý lỗi JSON malformed
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        log.warn("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(getMessage("error.malformed.json")));
    }

    /**
     * Xử lý lỗi HTTP method not supported
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {

        log.warn("HTTP method not supported: {}", ex.getMethod());
        String message = String.format("Phương thức HTTP '%s' không được hỗ trợ cho endpoint này",
                ex.getMethod());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(message));
    }

    /**
     * Xử lý lỗi 404 - Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoHandlerFoundException(
            NoHandlerFoundException ex) {

        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(getMessage("error.endpoint.not.found")));
    }

    /**
     * Xử lý lỗi file upload quá lớn
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex) {

        log.warn("Upload size exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error(getMessage("error.file.too.large")));
    }

    /**
     * Xử lý lỗi database constraint violation
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {

        log.warn("Data integrity violation: {}", ex.getMessage());

        // Có thể parse message để đưa ra thông báo cụ thể hơn
        String message = parseDataIntegrityViolationMessage(ex.getMessage());

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message));
    }

    /**
     * Xử lý lỗi business logic (từ validators và services)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.warn("Business logic error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi đăng nhập - sai credentials
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(
            BadCredentialsException ex) {

        log.warn("Bad credentials: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(getMessage("error.bad.credentials")));
    }

    /**
     * Xử lý lỗi không tìm thấy user
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(
            UsernameNotFoundException ex) {

        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(getMessage("error.bad.credentials")));
    }

    /**
     * Xử lý lỗi access denied (403)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {

        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(getMessage("error.access.denied")));
    }

    /**
     * Xử lý lỗi authentication (401)
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {

        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(getMessage("error.authentication.required")));
    }

    /**
     * Xử lý custom exception - Resource not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý custom exception - Data not found
     */
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleDataNotFoundException(
            DataNotFoundException ex) {

        log.warn("Data not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý custom exception - Validation
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(
            ValidationException ex) {

        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý custom exception - Business
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(
            BusinessException ex) {

        log.warn("Business logic error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý custom exception - Unauthorized
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedException(
            UnauthorizedException ex) {

        log.warn("Unauthorized access: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi RuntimeException (trừ các lỗi đã được handle ở trên)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        log.error("Runtime exception occurred at {}: ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(getMessage("error.internal.server")));
    }

    /**
     * Xử lý tất cả các lỗi khác không được handle cụ thể
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error occurred at {}: ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(getMessage("error.unexpected")));
    }

    /**
     * Helper method để lấy localized message
     */
    private String getMessage(String key, Object... args) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            // Fallback to default message if localization fails
            return getDefaultMessage(key);
        }
    }

    /**
     * Default messages khi không có MessageSource
     */
    private String getDefaultMessage(String key) {
        return switch (key) {
            case "error.validation.fields" -> "Dữ liệu không hợp lệ";
            case "error.validation.parameters" -> "Tham số không hợp lệ";
            case "error.malformed.json" -> "Dữ liệu JSON không hợp lệ";
            case "error.endpoint.not.found" -> "Không tìm thấy endpoint";
            case "error.file.too.large" -> "File tải lên quá lớn";
            case "error.bad.credentials" -> "Email/Username hoặc mật khẩu không đúng";
            case "error.access.denied" -> "Bạn không có quyền truy cập tài nguyên này";
            case "error.authentication.required" -> "Vui lòng đăng nhập để tiếp tục";
            case "error.internal.server" -> "Có lỗi xảy ra trong hệ thống";
            case "error.unexpected" -> "Đã xảy ra lỗi không mong muốn, vui lòng thử lại sau";
            default -> "Đã xảy ra lỗi";
        };
    }

    /**
     * Parse database constraint violation message
     */
    private String parseDataIntegrityViolationMessage(String message) {
        if (message == null) return "Lỗi ràng buộc dữ liệu";

        // Parse common constraint violations
        if (message.contains("Duplicate entry")) {
            return "Dữ liệu đã tồn tại trong hệ thống";
        }
        if (message.contains("foreign key constraint")) {
            return "Không thể thực hiện do ràng buộc dữ liệu liên quan";
        }
        if (message.contains("cannot be null")) {
            return "Thiếu thông tin bắt buộc";
        }

        return "Lỗi ràng buộc dữ liệu";
    }
}
