package com.project.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
@Schema(description = "Login isteği")
public class LoginRequest {

    @NotBlank(message = "Kullanıcı adı zorunludur")
    @Schema(example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED )
    private String username;

    @NotBlank(message = "Şifre zorunludur")
    @Schema(example = "secret123", requiredMode = Schema.RequiredMode.REQUIRED )
    private String password;
}