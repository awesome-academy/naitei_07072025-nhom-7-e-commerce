package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "{user.email.notblank}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotBlank(message = "{user.fullName.notblank}")
        @Size(min = 2, max = 100, message = "{user.fullName.size}")
        String fullName,

        @NotBlank(message = "{phone.notblank}")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "{phone.invalid}")
        String phone,

        String address
) {}
