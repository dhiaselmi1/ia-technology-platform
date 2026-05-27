package com.iatechnology.controller;

import com.iatechnology.model.User;
import com.iatechnology.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Current user profile management")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "Get current user profile")
    public ResponseEntity<Map<String, Object>> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getDisplayName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "active", user.isActive(),
                "createdAt", user.getCreatedAt().toString()
        ));
    }

    @PutMapping
    @Operation(summary = "Update current user profile (username, email)")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, String> body, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String newUsername = body.get("username");
        String newEmail = body.get("email");

        if (newUsername != null && !newUsername.isBlank()) {
            if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Ce nom d'utilisateur est déjà pris"));
            }
            user.setUsername(newUsername);
        }

        if (newEmail != null && !newEmail.isBlank()) {
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Cet email est déjà utilisé"));
            }
            user.setEmail(newEmail);
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getDisplayName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "active", user.isActive(),
                "createdAt", user.getCreatedAt().toString(),
                "message", "Profil mis à jour avec succès"
        ));
    }

    @PutMapping("/password")
    @Operation(summary = "Change current user password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> body, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Les deux champs sont obligatoires"));
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mot de passe actuel incorrect"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le nouveau mot de passe doit contenir au moins 6 caractères"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }
}
