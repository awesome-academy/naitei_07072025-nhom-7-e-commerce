package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyOtpDto {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "OTP không được để trống")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP phải có 6 chữ số")
    private String otp;

    // Constructors
    public VerifyOtpDto() {}

    public VerifyOtpDto(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}
