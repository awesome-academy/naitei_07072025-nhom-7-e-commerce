package com.group7.ecommerce.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.group7.ecommerce.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * Custom exception cho resource not found
	 */
	public static class ResourceNotFoundException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ResourceNotFoundException(String message) {
			super(message);
		}

		public ResourceNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Xử lý lỗi validation (@Valid annotation)
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
				.body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
	}

	/**
	 * Xử lý lỗi business logic (từ validators và services)
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.warn("Business logic error: {}", ex.getMessage());
		return ResponseEntity.badRequest()
				.body(ApiResponse.error(ex.getMessage()));
	}

	/**
	 * Xử lý lỗi đăng nhập - sai credentials
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex) {
		log.warn("Bad credentials: {}", ex.getMessage());
		return ResponseEntity.badRequest()
				.body(ApiResponse.error("Email/Username hoặc mật khẩu không đúng"));
	}

	/**
	 * Xử lý lỗi không tìm thấy user
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleUserNotFound(UsernameNotFoundException ex) {
		log.warn("User not found: {}", ex.getMessage());
		return ResponseEntity.badRequest()
				.body(ApiResponse.error("Email/Username hoặc mật khẩu không đúng"));
	}

	/**
	 * Xử lý lỗi access denied (403)
	 */
	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ApiResponse<String>> handleAccessDenied(
			org.springframework.security.access.AccessDeniedException ex) {
		log.warn("Access denied: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error("Bạn không có quyền truy cập tài nguyên này"));
	}

	/**
	 * Xử lý lỗi authentication (401)
	 */
	@ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
	public ResponseEntity<ApiResponse<String>> handleAuthenticationException(
			org.springframework.security.core.AuthenticationException ex) {
		log.warn("Authentication error: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error("Vui lòng đăng nhập để tiếp tục"));
	}

	/**
	 * Xử lý các custom exceptions nếu có
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex) {
		log.warn("Resource not found: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(ex.getMessage()));
	}

	/**
	 * Xử lý lỗi RuntimeException (trừ các lỗi đã được handle ở trên)
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
		log.error("Runtime exception occurred", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("Có lỗi xảy ra trong hệ thống"));
	}

	/**
	 * Xử lý tất cả các lỗi khác không được handle cụ thể
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
		log.error("Unexpected error occurred", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("Đã xảy ra lỗi không mong muốn, vui lòng thử lại sau"));
	}
}
