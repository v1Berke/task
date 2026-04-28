package com.project.taskmanager.config;

import com.project.taskmanager.entity.Task;
import com.project.taskmanager.entity.TaskStatus;
import com.project.taskmanager.entity.User;
import com.project.taskmanager.repository.TaskRepository;
import com.project.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Demo veri yükleyici.
 * DatabaseInitializer tabloları oluşturduktan sonra (@Order 2) çalışır.
 * Veritabanı boşsa demo kullanıcı ve görevler ekler.
 *
 * Demo:  username=demo  /  password=demo123
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            log.info("Mevcut veri bulundu, demo veri atlanıyor.");
            return;
        }

        log.info(">>> Demo veri yukleniyor...");

        User demo = userRepository.save(
                User.builder()
                        .username("demo")
                        .email("demo@example.com")
                        .password(passwordEncoder.encode("demo123"))
                        .build()
        );

        taskRepository.save(Task.builder()
                .title("Spring Boot projesini tamamla")
                .description("Controller, Service ve Repository katmanlarini yaz")
                .status(TaskStatus.IN_PROGRESS)
                .deadline(LocalDateTime.now().plusDays(3))
                .user(demo)
                .build());

        taskRepository.save(Task.builder()
                .title("Swagger dokumantasyonu ekle")
                .description("Tum endpointlere @Operation ve @ApiResponse ekle")
                .status(TaskStatus.TODO)
                .deadline(LocalDateTime.now().plusDays(7))
                .user(demo)
                .build());

        taskRepository.save(Task.builder()
                .title("Unit testleri yaz")
                .description("Service katmani icin JUnit 5 + Mockito testleri")
                .status(TaskStatus.DONE)
                .user(demo)
                .build());

        log.info(">>> Demo veri yuklendi.");
        log.info("    Kullanici   : demo");
        log.info("    Sifre       : demo123");
        log.info("    Gorev sayisi: 3");
        log.info("    Swagger UI  : http://localhost:8080/swagger-ui.html");
    }
}