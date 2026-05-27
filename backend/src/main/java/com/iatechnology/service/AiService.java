package com.iatechnology.service;

import com.iatechnology.dto.ai.KeywordsRequest;
import com.iatechnology.dto.ai.KeywordsResponse;
import com.iatechnology.dto.ai.RecommendationsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${app.ai.service.url}")
    private String aiServiceUrl;

    public KeywordsResponse extractKeywords(String text, int n) {
        try {
            KeywordsRequest request = KeywordsRequest.builder().text(text).n(n).build();
            KeywordsResponse response = restTemplate.postForObject(aiServiceUrl + "/keywords", request, KeywordsResponse.class);
            return response != null ? response : KeywordsResponse.builder().build();
        } catch (Exception e) {
            log.error("Error calling AI service for keywords extraction", e);
            return KeywordsResponse.builder().build();
        }
    }

    public RecommendationsResponse getRecommendations(String targetText, List<com.iatechnology.dto.PublicationDTO> corpus, int topN) {
        try {
            List<RecommendationCorpusItem> corpusItems = corpus.stream()
                    .map(p -> RecommendationCorpusItem.builder()
                            .id(p.getId())
                            .text(p.getTitle() + " " + p.getAbstract_())
                            .build())
                    .toList();

            RecommendRequest request = RecommendRequest.builder()
                    .target_text(targetText).corpus(corpusItems).top_n(topN).build();

            RecommendationsResponse response = restTemplate.postForObject(aiServiceUrl + "/recommend", request, RecommendationsResponse.class);
            return response != null ? response : RecommendationsResponse.builder().build();
        } catch (Exception e) {
            log.error("Error calling AI service for recommendations", e);
            return RecommendationsResponse.builder().build();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> semanticSearch(String query, List<com.iatechnology.dto.PublicationDTO> corpus, int topN) {
        try {
            List<Map<String, Object>> corpusList = corpus.stream()
                    .map(p -> Map.<String, Object>of("id", p.getId(), "text", p.getTitle() + " " + (p.getAbstract_() != null ? p.getAbstract_() : "")))
                    .toList();

            Map<String, Object> request = Map.of("query", query, "corpus", corpusList, "top_n", topN);
            return restTemplate.postForObject(aiServiceUrl + "/semantic-search", request, Map.class);
        } catch (Exception e) {
            log.error("Error calling AI service for semantic search", e);
            return Map.of("results", List.of());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> ask(String question, List<com.iatechnology.dto.PublicationDTO> corpus, int topN) {
        try {
            List<Map<String, Object>> corpusList = corpus.stream()
                    .map(p -> Map.<String, Object>of("id", p.getId(), "text", p.getTitle() + " " + (p.getAbstract_() != null ? p.getAbstract_() : "")))
                    .toList();

            Map<String, Object> request = Map.of("question", question, "corpus", corpusList, "top_n", topN);
            return restTemplate.postForObject(aiServiceUrl + "/ask", request, Map.class);
        } catch (Exception e) {
            log.error("Error calling AI service for Q&A", e);
            return Map.of("results", List.of());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> summarize(String text, String mode) {
        try {
            Map<String, Object> request = Map.of("text", text, "mode", mode);
            return restTemplate.postForObject(aiServiceUrl + "/summarize", request, Map.class);
        } catch (Exception e) {
            log.error("Error calling AI service for summarization", e);
            return Map.of("summary", "", "error", "Service indisponible");
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> researcherProfile(Long researcherId, List<String> researcherTexts, List<Map<String, Object>> allResearchers, int topN) {
        try {
            Map<String, Object> request = Map.of(
                    "researcher_id", researcherId,
                    "researcher_texts", researcherTexts,
                    "all_researchers", allResearchers,
                    "top_n", topN
            );
            return restTemplate.postForObject(aiServiceUrl + "/researcher-profile", request, Map.class);
        } catch (Exception e) {
            log.error("Error calling AI service for researcher profile", e);
            return Map.of("keywords", List.of(), "similar", List.of());
        }
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class RecommendRequest {
        private String target_text;
        private List<RecommendationCorpusItem> corpus;
        private int top_n;
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class RecommendationCorpusItem {
        private Long id;
        private String text;
    }
}
