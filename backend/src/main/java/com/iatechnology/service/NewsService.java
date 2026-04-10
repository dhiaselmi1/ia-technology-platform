package com.iatechnology.service;

import com.iatechnology.dto.NewsDTO;
import com.iatechnology.model.News;
import com.iatechnology.model.User;
import com.iatechnology.repository.NewsRepository;
import com.iatechnology.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<NewsDTO> getAll() {
        return newsRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    public List<NewsDTO> getFeatured() {
        return newsRepository.findByFeaturedTrueOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    public NewsDTO getById(Long id) {
        return newsRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée"));
    }

    @Transactional
    public NewsDTO create(NewsDTO dto, String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        News news = News.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .featured(dto.isFeatured())
                .author(author)
                .build();
        News saved = newsRepository.save(news);
        auditLogService.log("CREATE", "News", saved.getId(), email, "News created: " + saved.getTitle());
        return toDTO(saved);
    }

    @Transactional
    public NewsDTO update(Long id, NewsDTO dto, String email) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée"));
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setImageUrl(dto.getImageUrl());
        news.setFeatured(dto.isFeatured());
        News updated = newsRepository.save(news);
        auditLogService.log("UPDATE", "News", id, email, "News updated");
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id, String email) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("Actualité non trouvée");
        }
        newsRepository.deleteById(id);
        auditLogService.log("DELETE", "News", id, email, "News deleted");
    }

    private NewsDTO toDTO(News news) {
        return NewsDTO.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .imageUrl(news.getImageUrl())
                .featured(news.isFeatured())
                .authorUsername(news.getAuthor().getUsername())
                .createdAt(news.getCreatedAt())
                .build();
    }
}
