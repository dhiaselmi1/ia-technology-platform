package com.iatechnology.dto;

import jakarta.validation.constraints.NotBlank;
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
public class NewsDTO {
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne doit pas depasser 200 caracteres")
    private String title;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(max = 3000, message = "Le contenu ne doit pas depasser 3000 caracteres")
    private String content;
    private String imageUrl;
    private boolean featured;
    private String authorUsername;
    private LocalDateTime createdAt;
}
