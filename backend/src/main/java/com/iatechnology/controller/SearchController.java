package com.iatechnology.controller;

import com.iatechnology.dto.PublicationDTO;
import com.iatechnology.dto.ResearcherDTO;
import com.iatechnology.service.PublicationService;
import com.iatechnology.service.ResearcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Global search endpoints")
public class SearchController {

    private final PublicationService publicationService;
    private final ResearcherService researcherService;

    @GetMapping
    @Operation(summary = "Global search across publications and researchers")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long domain,
            @RequestParam(required = false) Long researcher) {

        Map<String, Object> result = new HashMap<>();

        if (q != null && !q.isBlank()) {
            List<PublicationDTO> publications = publicationService.searchByQuery(q);
            List<ResearcherDTO> researchers = researcherService.searchByName(q);
            result.put("publications", publications);
            result.put("researchers", researchers);
        }

        if (domain != null) {
            result.put("publicationsByDomain", publicationService.getByDomain(domain));
            result.put("researchersByDomain", researcherService.getByDomain(domain));
        }

        if (researcher != null) {
            result.put("publicationsByResearcher", publicationService.getByResearcher(researcher));
        }

        return ResponseEntity.ok(result);
    }
}
