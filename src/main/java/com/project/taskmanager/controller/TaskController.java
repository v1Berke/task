package com.project.taskmanager.controller;

import com.project.taskmanager.dto.TaskCreateRequest;
import com.project.taskmanager.dto.TaskResponse;
import com.project.taskmanager.dto.TaskUpdateRequest;
import com.project.taskmanager.entity.TaskStatus;
import com.project.taskmanager.entity.User;
import com.project.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Görev yönetimi (CRUD)")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(
            summary = "Görevleri listele",
            description = "Giriş yapmış kullanıcının tüm görevlerini döner. " +
                    "İsteğe bağlı 'status' parametresi ile filtrelenebilir: TODO | IN_PROGRESS | DONE"
    )
    public ResponseEntity<List<TaskResponse>> list(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) TaskStatus status) {
        List<TaskResponse> tasks = status != null
                ? taskService.getTasksByStatus(user, status)
                : taskService.getAllTasks(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Görev detayı")
    public ResponseEntity<TaskResponse> get(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(user, id));
    }

    @PostMapping
    @Operation(summary = "Yeni görev oluştur")
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(user, request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Görevi güncelle",
            description = "Tüm alanlar opsiyoneldir — sadece gönderilen alanlar güncellenir."
    )
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTask(user, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Görevi sil")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        taskService.deleteTask(user, id);
        return ResponseEntity.noContent().build();
    }
}
