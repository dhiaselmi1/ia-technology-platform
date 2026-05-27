package com.iatechnology.dto;

import jakarta.validation.constraints.Email;
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
public class ResearcherDTO {
    private Long id;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 50, message = "Le prenom ne doit pas depasser 50 caracteres")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas depasser 50 caracteres")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit etre valide")
    private String email;

    @Size(max = 1000, message = "La bio ne doit pas depasser 1000 caracteres")
    private String bio;
    private String photoUrl;
    @NotNull(message = "Le domaine est obligatoire")
    private Long domainId;
    private String domainName;
    private LocalDateTime createdAt;
}
