package com.iatechnology.controller;

import com.iatechnology.dto.UserDTO;
import com.iatechnology.model.Role;
import com.iatechnology.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints (ADMIN only)")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (ADMIN only)")
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID (ADMIN only)")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user (ADMIN only)")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto, auth.getName()));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role (ADMIN only)")
    public ResponseEntity<UserDTO> updateRole(@PathVariable Long id, @RequestParam Role role, Authentication auth) {
        return ResponseEntity.ok(userService.updateRole(id, role, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        userService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
