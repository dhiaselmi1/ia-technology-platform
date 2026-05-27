package com.iatechnology.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 300, message = "Le titre ne doit pas depasser 300 caracteres")
    private String title;

    @JsonProperty("abstractText")
    @Size(max = 2000, message = "Le resume ne doit pas depasser 2000 caracteres")
    private String abstract_;

    private String doi;
    private String filePath;
    private LocalDateTime publishedDate;
    @NotNull(message = "Le chercheur est obligatoire")
    private Long researcherId;
    private String researcherName;

    @NotNull(message = "Le domaine est obligatoire")
    private Long domainId;
    private String domainName;
    private boolean featured;
    private LocalDateTime createdAt;
}
