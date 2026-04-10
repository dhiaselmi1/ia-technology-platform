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
public class DomainDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private LocalDateTime createdAt;
}
