package com.iatechnology.controller;

import com.iatechnology.dto.PublicationDTO;
import com.iatechnology.dto.ResearcherDTO;
import com.iatechnology.dto.ai.KeywordsResponse;
import com.iatechnology.dto.ai.RecommendationsResponse;
import com.iatechnology.service.AiService;
import com.iatechnology.service.PublicationService;
import com.iatechnology.service.ResearcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "AI", description = "AI-powered features")
public class AiController {

    private final AiService aiService;
    private final PublicationService publicationService;
    private final ResearcherService researcherService;

    @PostMapping("/keywords")
    @Operation(summary = "Extract keywords from text using TF-IDF")
    public ResponseEntity<KeywordsResponse> extractKeywords(
            @RequestParam String text,
            @RequestParam(defaultValue = "10") int n) {
        return ResponseEntity.ok(aiService.extractKeywords(text, n));
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
            return ResponseEntity.ok(aiService.getRecommendations(targetText, corpus, topN));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/semantic-search")
    @Operation(summary = "Semantic search across publications")
    public ResponseEntity<Map<String, Object>> semanticSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int topN) {
        var corpus = publicationService.getAll();
        return ResponseEntity.ok(aiService.semanticSearch(query, corpus, topN));
    }

    @PostMapping("/ask")
    @Operation(summary = "Q&A — find answers from publications")
    public ResponseEntity<Map<String, Object>> ask(
            @RequestParam String question,
            @RequestParam(defaultValue = "5") int topN) {
        var corpus = publicationService.getAll();
        return ResponseEntity.ok(aiService.ask(question, corpus, topN));
    }

    @PostMapping("/summarize")
    @Operation(summary = "Summarize text (tldr or vulgarize)")
    public ResponseEntity<Map<String, Object>> summarize(
            @RequestParam String text,
            @RequestParam(defaultValue = "tldr") String mode) {
        return ResponseEntity.ok(aiService.summarize(text, mode));
    }

    @PostMapping("/classify")
    @Operation(summary = "Classify publication text into a domain")
    public ResponseEntity<Map<String, Object>> classify(
            @RequestParam String text,
            @RequestParam(defaultValue = "3") int topN) {
        var corpus = publicationService.getAll();
        return ResponseEntity.ok(aiService.classifyDomain(text, corpus, topN));
    }

    @GetMapping("/predict-trends")
    @Operation(summary = "Predict domain publication trends using Random Forest classifier")
    public ResponseEntity<Map<String, Object>> predictTrends() {
        var pubs = publicationService.getAll();
        return ResponseEntity.ok(aiService.predictTrends(pubs));
    }

    @GetMapping("/researcher-profile/{researcherId}")
    @Operation(summary = "Get AI-generated researcher profile with keywords and similar researchers")
    public ResponseEntity<Map<String, Object>> researcherProfile(
            @PathVariable Long researcherId,
            @RequestParam(defaultValue = "3") int topN) {
        try {
            var allPubs = publicationService.getAll();
            var allResearchers = researcherService.getAll();

            List<String> targetTexts = allPubs.stream()
                    .filter(p -> p.getResearcherId() != null && p.getResearcherId().equals(researcherId))
                    .map(p -> p.getTitle() + " " + (p.getAbstract_() != null ? p.getAbstract_() : ""))
                    .toList();

            List<Map<String, Object>> researcherList = new ArrayList<>();
            for (ResearcherDTO r : allResearchers) {
                List<String> texts = allPubs.stream()
                        .filter(p -> p.getResearcherId() != null && p.getResearcherId().equals(r.getId()))
                        .map(p -> p.getTitle() + " " + (p.getAbstract_() != null ? p.getAbstract_() : ""))
                        .toList();
                Map<String, Object> entry = new HashMap<>();
                entry.put("id", r.getId());
                entry.put("name", r.getFirstName() + " " + r.getLastName());
                entry.put("domain", r.getDomainName());
                entry.put("texts", texts);
                researcherList.add(entry);
            }

            return ResponseEntity.ok(aiService.researcherProfile(researcherId, targetTexts, researcherList, topN));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("keywords", List.of(), "similar", List.of()));
        }
    }
}
