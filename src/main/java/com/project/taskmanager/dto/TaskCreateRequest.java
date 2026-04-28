package com.project.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Yeni görev oluşturma isteği")
public class TaskCreateRequest {

    @NotBlank(message = "Başlık zorunludur")
    @Schema(description = "Görev başlığı", example = "API endpoint'lerini yaz", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Görev açıklaması", example = "Tüm CRUD endpoint'leri implement et")
    private String description;

    @Schema(description = "Son tarih", example = "2024-12-31T23:59:00")
    private LocalDateTime deadline;
}