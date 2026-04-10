package com.iatechnology.service;

import com.iatechnology.dto.DomainDTO;
import com.iatechnology.model.Domain;
import com.iatechnology.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;
    private final AuditLogService auditLogService;

    public List<DomainDTO> getAll() {
        return domainRepository.findAll().stream().map(this::toDTO).toList();
    }

    public DomainDTO getById(Long id) {
        return domainRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Domaine non trouvé"));
    }

    @Transactional
    public DomainDTO create(DomainDTO dto, String username) {
        if (domainRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Ce domaine existe déjà");
        }
        Domain domain = Domain.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .build();
        Domain saved = domainRepository.save(domain);
        auditLogService.log("CREATE", "Domain", saved.getId(), username, "Domain created: " + saved.getName());
        return toDTO(saved);
    }

    @Transactional
    public DomainDTO update(Long id, DomainDTO dto, String username) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Domaine non trouvé"));
        domain.setName(dto.getName());
        domain.setDescription(dto.getDescription());
        domain.setCategory(dto.getCategory());
        Domain updated = domainRepository.save(domain);
        auditLogService.log("UPDATE", "Domain", id, username, "Domain updated");
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id, String username) {
        if (!domainRepository.existsById(id)) {
            throw new RuntimeException("Domaine non trouvé");
        }
        domainRepository.deleteById(id);
        auditLogService.log("DELETE", "Domain", id, username, "Domain deleted");
    }

    private DomainDTO toDTO(Domain domain) {
        return DomainDTO.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
