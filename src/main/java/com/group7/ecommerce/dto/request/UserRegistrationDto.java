package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegistrationDto {

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotBlank(message = "{user.fullName.notblank}")
    @Size(min = 2, max = 100, message = "{user.fullName.size}")
    private String fullName;

    @NotBlank(message = "{user.username.notblank}")
    @Size(min = 2, max = 100, message = "{user.username.size}")
    private String username;

    @NotBlank(message = "{phone.notblank}")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "{phone.invalid}")
    private String phone;

    @NotBlank(message = "{user.password.notblank}")
    @Size(min = 8, message = "{user.password.size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "{user.password.pattern}")
    private String password;
}
