package com.project.taskmanager.repository;

import com.project.taskmanager.entity.Task;
import com.project.taskmanager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Task için veri erişim katmanı.
 * JpaRepository sayesinde CRUD operasyonları otomatik gelir.
 * İhtiyaca özel sorgular method ismiyle veya @Query ile tanımlanır.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Kullanıcıya ait tüm görevleri getir
    List<Task> findByUserId(Long userId);

    // Kullanıcıya ait, belirli statüsteki görevleri getir
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    // Belirli bir göreve kullanıcının erişimi var mı kontrolü
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    // Deadline geçmiş ama tamamlanmamış görevler
    List<Task> findByUserIdAndStatusNotAndDeadlineBefore(
            Long userId, TaskStatus status, LocalDateTime dateTime
    );
}