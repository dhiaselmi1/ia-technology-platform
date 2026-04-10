package com.iatechnology.controller;

import com.iatechnology.dto.DomainDTO;
import com.iatechnology.service.DomainService;
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
@RequestMapping("/api/domains")
@RequiredArgsConstructor
@Tag(name = "Domains", description = "Domain management endpoints")
public class DomainController {

    private final DomainService domainService;

    @GetMapping
    @Operation(summary = "Get all domains")
    public ResponseEntity<List<DomainDTO>> getAll() {
        return ResponseEntity.ok(domainService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get domain by ID")
    public ResponseEntity<DomainDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(domainService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new domain (ADMIN only)")
    public ResponseEntity<DomainDTO> create(@Valid @RequestBody DomainDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(domainService.create(dto, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update domain (ADMIN only)")
    public ResponseEntity<DomainDTO> update(@PathVariable Long id, @Valid @RequestBody DomainDTO dto, Authentication auth) {
        return ResponseEntity.ok(domainService.update(id, dto, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete domain (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        domainService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
