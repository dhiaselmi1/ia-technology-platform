package com.iatechnology.service;

import com.iatechnology.dto.PublicationDTO;
import com.iatechnology.model.Domain;
import com.iatechnology.model.Publication;
import com.iatechnology.model.Researcher;
import com.iatechnology.repository.DomainRepository;
import com.iatechnology.repository.PublicationRepository;
import com.iatechnology.repository.ResearcherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final ResearcherRepository researcherRepository;
    private final DomainRepository domainRepository;
    private final AuditLogService auditLogService;

    public List<PublicationDTO> getAll() {
        return publicationRepository.findAll().stream().map(this::toDTO).toList();
    }

    public PublicationDTO getById(Long id) {
        return publicationRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
    }

    public List<PublicationDTO> searchByQuery(String query) {
        return publicationRepository.searchByTitleOrAbstract(query).stream().map(this::toDTO).toList();
    }

    public List<PublicationDTO> getByResearcher(Long researcherId) {
        return publicationRepository.findByResearcherId(researcherId).stream().map(this::toDTO).toList();
    }

    public List<PublicationDTO> getByDomain(Long domainId) {
        return publicationRepository.findByDomainId(domainId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public PublicationDTO create(PublicationDTO dto, String username) {
        Researcher researcher = researcherRepository.findById(dto.getResearcherId())
                .orElseThrow(() -> new RuntimeException("Chercheur non trouvé"));
        Domain domain = domainRepository.findById(dto.getDomainId())
                .orElseThrow(() -> new RuntimeException("Domaine non trouvé"));

        Publication publication = Publication.builder()
                .title(dto.getTitle())
                .abstract_(dto.getAbstract_())
                .doi(dto.getDoi())
                .filePath(dto.getFilePath())
                .publishedDate(dto.getPublishedDate())
                .researcher(researcher)
                .domain(domain)
                .build();
        Publication saved = publicationRepository.save(publication);
        auditLogService.log("CREATE", "Publication", saved.getId(), username, "Publication created: " + saved.getTitle());
        return toDTO(saved);
    }

    @Transactional
    public PublicationDTO update(Long id, PublicationDTO dto, String username) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
        publication.setTitle(dto.getTitle());
        publication.setAbstract_(dto.getAbstract_());
        publication.setDoi(dto.getDoi());
        publication.setFilePath(dto.getFilePath());
        publication.setPublishedDate(dto.getPublishedDate());
        Publication updated = publicationRepository.save(publication);
        auditLogService.log("UPDATE", "Publication", id, username, "Publication updated");
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id, String username) {
        if (!publicationRepository.existsById(id)) {
            throw new RuntimeException("Publication non trouvée");
        }
        publicationRepository.deleteById(id);
        auditLogService.log("DELETE", "Publication", id, username, "Publication deleted");
    }

    private PublicationDTO toDTO(Publication publication) {
        return PublicationDTO.builder()
                .id(publication.getId())
                .title(publication.getTitle())
                .abstract_(publication.getAbstract_())
                .doi(publication.getDoi())
                .filePath(publication.getFilePath())
                .publishedDate(publication.getPublishedDate())
                .researcherId(publication.getResearcher().getId())
                .researcherName(publication.getResearcher().getFirstName() + " " + publication.getResearcher().getLastName())
                .domainId(publication.getDomain().getId())
                .domainName(publication.getDomain().getName())
                .createdAt(publication.getCreatedAt())
                .build();
    }
}
