package com.iatechnology.repository;

import com.iatechnology.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {
    List<Domain> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}
