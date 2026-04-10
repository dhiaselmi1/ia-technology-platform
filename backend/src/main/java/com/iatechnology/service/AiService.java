package com.iatechnology.service;

import com.iatechnology.dto.ai.KeywordsRequest;
import com.iatechnology.dto.ai.KeywordsResponse;
import com.iatechnology.dto.ai.RecommendationsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${app.ai.service.url}")
    private String aiServiceUrl;

    public KeywordsResponse extractKeywords(String text, int n) {
        try {
            KeywordsRequest request = KeywordsRequest.builder()
                    .text(text)
                    .n(n)
                    .build();

            KeywordsResponse response = restTemplate.postForObject(
                    aiServiceUrl + "/keywords",
                    request,
                    KeywordsResponse.class
            );

            return response != null ? response : KeywordsResponse.builder().build();
        } catch (Exception e) {
            log.error("Error calling AI service for keywords extraction", e);
            return KeywordsResponse.builder().build();
        }
    }

    public RecommendationsResponse getRecommendations(String targetText, java.util.List<com.iatechnology.dto.PublicationDTO> corpus, int topN) {
        try {
            java.util.List<RecommendationCorpusItem> corpusItems = corpus.stream()
                    .map(p -> RecommendationCorpusItem.builder()
                            .id(p.getId())
                            .text(p.getTitle() + " " + p.getAbstract_())
                            .build())
                    .toList();

            RecommendRequest request = RecommendRequest.builder()
                    .target_text(targetText)
                    .corpus(corpusItems)
                    .top_n(topN)
                    .build();

            RecommendationsResponse response = restTemplate.postForObject(
                    aiServiceUrl + "/recommend",
                    request,
                    RecommendationsResponse.class
            );

            return response != null ? response : RecommendationsResponse.builder().build();
        } catch (Exception e) {
            log.error("Error calling AI service for recommendations", e);
            return RecommendationsResponse.builder().build();
        }
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RecommendRequest {
        private String target_text;
        private java.util.List<RecommendationCorpusItem> corpus;
        private int top_n;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RecommendationCorpusItem {
        private Long id;
        private String text;
    }
}
