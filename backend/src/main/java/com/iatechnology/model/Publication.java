package com.iatechnology.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "publications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(length = 2000)
    private String abstract_;

    @Column(unique = true, length = 50)
    private String doi;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @ManyToOne
    @JoinColumn(name = "researcher_id", nullable = false)
    private Researcher researcher;

    @ManyToOne
    @JoinColumn(name = "domain_id", nullable = false)
    private Domain domain;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
