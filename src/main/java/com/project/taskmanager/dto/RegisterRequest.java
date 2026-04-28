package com.project.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Kayıt isteği")
public class RegisterRequest {

    @NotBlank(message = "Kullanıcı adı zorunludur")
    @Schema(example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Email(message = "Geçerli e-posta giriniz")
    @NotBlank(message = "E-posta zorunludur")
    @Schema(example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Şifre zorunludur")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalı")
    @Schema(example = "secret123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}