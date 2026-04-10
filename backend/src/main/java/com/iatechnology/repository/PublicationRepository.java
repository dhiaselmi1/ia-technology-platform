package com.iatechnology.repository;

import com.iatechnology.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByResearcherId(Long researcherId);
    List<Publication> findByDomainId(Long domainId);

    @Query("SELECT p FROM Publication p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.abstract_) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Publication> searchByTitleOrAbstract(String query);
}
