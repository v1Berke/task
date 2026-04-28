package com.project.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Authentication yanıtı.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication yanıtı")
public class AuthResponse {

    @Schema(example = "johndoe")
    private String username;

    @Schema(example = "john@example.com")
    private String email;

    @Schema(example = "1")
    private Long id;
}