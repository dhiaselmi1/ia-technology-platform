package com.iatechnology.service;

import com.iatechnology.dto.ResearcherDTO;
import com.iatechnology.model.Domain;
import com.iatechnology.model.Researcher;
import com.iatechnology.repository.DomainRepository;
import com.iatechnology.repository.ResearcherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResearcherService {

    private final ResearcherRepository researcherRepository;
    private final DomainRepository domainRepository;
    private final AuditLogService auditLogService;

    public List<ResearcherDTO> getAll() {
        return researcherRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ResearcherDTO getById(Long id) {
        return researcherRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Chercheur non trouvé"));
    }

    public List<ResearcherDTO> searchByName(String query) {
        return researcherRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream().map(this::toDTO).toList();
    }

    public List<ResearcherDTO> getByDomain(Long domainId) {
        return researcherRepository.findByDomainId(domainId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public ResearcherDTO create(ResearcherDTO dto, String username) {
        if (researcherRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        Domain domain = domainRepository.findById(dto.getDomainId())
                .orElseThrow(() -> new RuntimeException("Domaine non trouvé"));

        Researcher researcher = Researcher.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .bio(dto.getBio())
                .photoUrl(dto.getPhotoUrl())
                .domain(domain)
                .build();
        Researcher saved = researcherRepository.save(researcher);
        auditLogService.log("CREATE", "Researcher", saved.getId(), username, "Researcher created: " + saved.getFirstName() + " " + saved.getLastName());
        return toDTO(saved);
    }

    @Transactional
    public ResearcherDTO update(Long id, ResearcherDTO dto, String username) {
        Researcher researcher = researcherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chercheur non trouvé"));
        researcher.setFirstName(dto.getFirstName());
        researcher.setLastName(dto.getLastName());
        researcher.setEmail(dto.getEmail());
        researcher.setBio(dto.getBio());
        researcher.setPhotoUrl(dto.getPhotoUrl());
        Researcher updated = researcherRepository.save(researcher);
        auditLogService.log("UPDATE", "Researcher", id, username, "Researcher updated");
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id, String username) {
        if (!researcherRepository.existsById(id)) {
            throw new RuntimeException("Chercheur non trouvé");
        }
        researcherRepository.deleteById(id);
        auditLogService.log("DELETE", "Researcher", id, username, "Researcher deleted");
    }

    private ResearcherDTO toDTO(Researcher researcher) {
        return ResearcherDTO.builder()
                .id(researcher.getId())
                .firstName(researcher.getFirstName())
                .lastName(researcher.getLastName())
                .email(researcher.getEmail())
                .bio(researcher.getBio())
                .photoUrl(researcher.getPhotoUrl())
                .domainId(researcher.getDomain().getId())
                .domainName(researcher.getDomain().getName())
                .createdAt(researcher.getCreatedAt())
                .build();
    }
}
