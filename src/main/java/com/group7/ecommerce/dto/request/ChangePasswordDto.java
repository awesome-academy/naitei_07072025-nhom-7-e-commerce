package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto (
        @NotBlank(message = "{user.password.notblank}")
        String oldPassword,

        @NotBlank(message = "{user.password.notblank}")
        @Size(min = 8, message = "{user.password.size}")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "{user.password.pattern}")
        String newPassword,

        @NotBlank(message = "{user.password.notblank}")
        String confirmNewPassword
) {}
