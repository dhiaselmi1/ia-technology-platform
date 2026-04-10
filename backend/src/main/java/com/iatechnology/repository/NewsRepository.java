package com.iatechnology.repository;

import com.iatechnology.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByFeaturedTrueOrderByCreatedAtDesc();
    List<News> findAllByOrderByCreatedAtDesc();
}
