package com.iatechnology.controller;

import com.iatechnology.dto.ResearcherDTO;
import com.iatechnology.service.ResearcherService;
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
@RequestMapping("/api/researchers")
@RequiredArgsConstructor
@Tag(name = "Researchers", description = "Researcher management endpoints")
public class ResearcherController {

    private final ResearcherService researcherService;

    @GetMapping
    @Operation(summary = "Get all researchers")
    public ResponseEntity<List<ResearcherDTO>> getAll() {
        return ResponseEntity.ok(researcherService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get researcher by ID")
    public ResponseEntity<ResearcherDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(researcherService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search researchers by name")
    public ResponseEntity<List<ResearcherDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(researcherService.searchByName(query));
    }

    @GetMapping("/domain/{domainId}")
    @Operation(summary = "Get researchers by domain")
    public ResponseEntity<List<ResearcherDTO>> getByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(researcherService.getByDomain(domainId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new researcher (ADMIN only)")
    public ResponseEntity<ResearcherDTO> create(@Valid @RequestBody ResearcherDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(researcherService.create(dto, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update researcher (ADMIN only)")
    public ResponseEntity<ResearcherDTO> update(@PathVariable Long id, @Valid @RequestBody ResearcherDTO dto, Authentication auth) {
        return ResponseEntity.ok(researcherService.update(id, dto, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete researcher (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        researcherService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
