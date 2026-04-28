package com.project.taskmanager.service;

import com.project.taskmanager.dto.TaskCreateRequest;
import com.project.taskmanager.dto.TaskResponse;
import com.project.taskmanager.dto.TaskUpdateRequest;
import com.project.taskmanager.entity.Task;
import com.project.taskmanager.entity.TaskStatus;
import com.project.taskmanager.entity.User;
import com.project.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<TaskResponse> getAllTasks(User user) {
        return taskRepository.findByUserId(user.getId())
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    public List<TaskResponse> getTasksByStatus(User user, TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(user.getId(), status)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse getTask(User user, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Görev bulunamadı: " + taskId));
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse createTask(User user, TaskCreateRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .user(user)
                .build();
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(User user, Long taskId, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Görev bulunamadı: " + taskId));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(User user, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Görev bulunamadı: " + taskId));
        taskRepository.delete(task);
    }
}
