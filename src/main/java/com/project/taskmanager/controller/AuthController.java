package com.project.taskmanager.controller;

import com.project.taskmanager.dto.AuthResponse;
import com.project.taskmanager.dto.LoginRequest;
import com.project.taskmanager.dto.RegisterRequest;
import com.project.taskmanager.entity.User;
import com.project.taskmanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Kayıt, giriş ve oturum yönetimi")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Yeni kullanıcı kaydı")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Kayıt başarılı",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz veri"),
            @ApiResponse(responseCode = "409", description = "Kullanıcı adı veya e-posta zaten kayıtlı")
    })
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            AuthResponse response = authService.register(request, httpRequest, httpResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Kullanıcı girişi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Giriş başarılı",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Kullanıcı adı veya şifre yanlış")
    })
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            AuthResponse response = authService.login(request, httpRequest, httpResponse);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Kullanıcı adı veya şifre yanlış"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Çıkış yap")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Çıkış yapıldı"));
    }

    @GetMapping("/me")
    @Operation(summary = "Giriş yapmış kullanıcı bilgisi")
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new UserInfoResponse(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail()
        ));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Şifre değiştir")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(
                    currentUser.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla değiştirildi"));
        } catch (BadCredentialsException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @Schema(description = "Kullanıcı bilgisi yanıtı")
    public record UserInfoResponse(Long id, String username, String email) {}

    @Data
    @Schema(description = "Şifre değiştirme isteği")
    public static class ChangePasswordRequest {

        @NotBlank(message = "Mevcut şifre zorunludur")
        private String oldPassword;

        @NotBlank(message = "Yeni şifre zorunludur")
        @Size(min = 6, message = "Yeni şifre en az 6 karakter olmalı")
        private String newPassword;
    }
}
