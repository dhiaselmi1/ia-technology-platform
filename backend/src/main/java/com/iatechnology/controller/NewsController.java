package com.iatechnology.controller;

import com.iatechnology.dto.NewsDTO;
import com.iatechnology.service.NewsService;
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
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "News and announcements endpoints")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    @Operation(summary = "Get all news")
    public ResponseEntity<List<NewsDTO>> getAll() {
        return ResponseEntity.ok(newsService.getAll());
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured news")
    public ResponseEntity<List<NewsDTO>> getFeatured() {
        return ResponseEntity.ok(newsService.getFeatured());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get news by ID")
    public ResponseEntity<NewsDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MODERATEUR')")
    @Operation(summary = "Create news (MODERATEUR only)")
    public ResponseEntity<NewsDTO> create(@Valid @RequestBody NewsDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(dto, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATEUR')")
    @Operation(summary = "Update news (MODERATEUR only)")
    public ResponseEntity<NewsDTO> update(@PathVariable Long id, @Valid @RequestBody NewsDTO dto, Authentication auth) {
        return ResponseEntity.ok(newsService.update(id, dto, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATEUR')")
    @Operation(summary = "Delete news (MODERATEUR only)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        newsService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
