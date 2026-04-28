package com.project.taskmanager.dto;

import com.project.taskmanager.entity.Task;
import com.project.taskmanager.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Görev yanıtı")
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}