package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerifyOtpDto {

    @NotBlank(message = "{otp.email.notblank}")
    @Email(message = "{otp.email.invalid}")
    private String email;

    @NotBlank(message = "{otp.code.notblank}")
    @Pattern(regexp = "^[0-9]{6}$", message = "{otp.code.invalid}")
    private String otp;
}
