package com.group7.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank(message = "{emailOrUsername.notblank}")
    private String emailOrUsername;

    @NotBlank(message = "{password.notblank}")
    private String password;

}
