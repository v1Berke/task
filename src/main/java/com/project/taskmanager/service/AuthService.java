package com.project.taskmanager.service;

import com.project.taskmanager.dto.AuthResponse;
import com.project.taskmanager.dto.LoginRequest;
import com.project.taskmanager.dto.RegisterRequest;
import com.project.taskmanager.entity.User;
import com.project.taskmanager.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // Spring Security 6'da context kaydı için explicit repository kullanımı gerekli
    private final HttpSessionSecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @Transactional
    public AuthResponse register(RegisterRequest request,
                                 HttpServletRequest httpRequest,
                                 HttpServletResponse httpResponse) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Bu kullanıcı adı zaten kullanılıyor: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Bu e-posta zaten kayıtlı: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        saveSession(auth, httpRequest, httpResponse);

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request,
                              HttpServletRequest httpRequest,
                              HttpServletResponse httpResponse) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        saveSession(auth, httpRequest, httpResponse);

        User user = (User) auth.getPrincipal();
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Mevcut şifre yanlış");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Yeni şifre en az 6 karakter olmalı");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void saveSession(Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        // Spring Security 6: saveContext() ile HTTP session'a yazılır ve cookie set edilir
        securityContextRepository.saveContext(context, request, response);
    }
}
