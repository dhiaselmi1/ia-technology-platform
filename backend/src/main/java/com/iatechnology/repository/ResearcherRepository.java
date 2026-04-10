package com.iatechnology.repository;

import com.iatechnology.model.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearcherRepository extends JpaRepository<Researcher, Long> {
    List<Researcher> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    List<Researcher> findByDomainId(Long domainId);
    boolean existsByEmail(String email);
}
