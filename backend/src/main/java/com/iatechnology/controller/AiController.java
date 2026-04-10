package com.iatechnology.controller;

import com.iatechnology.dto.ai.KeywordsResponse;
import com.iatechnology.dto.ai.RecommendationsResponse;
import com.iatechnology.service.AiService;
import com.iatechnology.service.PublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI", description = "AI-powered features (keywords extraction, recommendations)")
public class AiController {

    private final AiService aiService;
    private final PublicationService publicationService;

    @PostMapping("/keywords")
    @Operation(summary = "Extract keywords from text using TF-IDF")
    public ResponseEntity<KeywordsResponse> extractKeywords(
            @RequestParam String text,
            @RequestParam(defaultValue = "10") int n) {
        KeywordsResponse response = aiService.extractKeywords(text, n);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommend/{publicationId}")
    @Operation(summary = "Get publication recommendations based on similarity")
    public ResponseEntity<RecommendationsResponse> getRecommendations(
            @PathVariable Long publicationId,
            @RequestParam(defaultValue = "5") int topN) {
        try {
            var targetPub = publicationService.getById(publicationId);
            var corpus = publicationService.getAll();
            String targetText = targetPub.getTitle() + " " + targetPub.getAbstract_();
            RecommendationsResponse response = aiService.getRecommendations(targetText, corpus, topN);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
