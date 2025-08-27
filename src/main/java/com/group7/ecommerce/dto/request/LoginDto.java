package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank(message = "{user.username.notblank}")
    private String emailOrUsername;

    @NotBlank(message = "{user.password.notblank}")
    private String password;

}
