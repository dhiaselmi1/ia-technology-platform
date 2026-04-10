package com.iatechnology.controller;

import com.iatechnology.dto.PublicationDTO;
import com.iatechnology.service.PublicationService;
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
@RequestMapping("/api/publications")
@RequiredArgsConstructor
@Tag(name = "Publications", description = "Publication management endpoints")
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping
    @Operation(summary = "Get all publications")
    public ResponseEntity<List<PublicationDTO>> getAll() {
        return ResponseEntity.ok(publicationService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get publication by ID")
    public ResponseEntity<PublicationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search publications by title or abstract")
    public ResponseEntity<List<PublicationDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(publicationService.searchByQuery(query));
    }

    @GetMapping("/researcher/{researcherId}")
    @Operation(summary = "Get publications by researcher")
    public ResponseEntity<List<PublicationDTO>> getByResearcher(@PathVariable Long researcherId) {
        return ResponseEntity.ok(publicationService.getByResearcher(researcherId));
    }

    @GetMapping("/domain/{domainId}")
    @Operation(summary = "Get publications by domain")
    public ResponseEntity<List<PublicationDTO>> getByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(publicationService.getByDomain(domainId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new publication (ADMIN only)")
    public ResponseEntity<PublicationDTO> create(@Valid @RequestBody PublicationDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicationService.create(dto, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update publication (ADMIN only)")
    public ResponseEntity<PublicationDTO> update(@PathVariable Long id, @Valid @RequestBody PublicationDTO dto, Authentication auth) {
        return ResponseEntity.ok(publicationService.update(id, dto, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete publication (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        publicationService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
