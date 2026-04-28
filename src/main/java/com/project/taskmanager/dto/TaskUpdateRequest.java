package com.project.taskmanager.dto;

import com.project.taskmanager.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Görev güncelleme isteği - Tüm alanlar opsiyoneldir (partial update)")
public class TaskUpdateRequest {

    @Schema(description = "Yeni başlık", example = "Güncellenen başlık")
    private String title;

    @Schema(description = "Yeni açıklama")
    private String description;

    @Schema(description = "Yeni durum", example = "IN_PROGRESS",
            allowableValues = {"TODO", "IN_PROGRESS", "DONE"})
    private TaskStatus status;

    @Schema(description = "Yeni son tarih")
    private LocalDateTime deadline;
}