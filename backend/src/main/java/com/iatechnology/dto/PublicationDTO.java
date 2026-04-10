package com.iatechnology.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDTO {
    private Long id;
    private String title;
    private String abstract_;
    private String doi;
    private String filePath;
    private LocalDateTime publishedDate;
    private Long researcherId;
    private String researcherName;
    private Long domainId;
    private String domainName;
    private LocalDateTime createdAt;
}
