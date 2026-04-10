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
public class NewsDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private boolean featured;
    private String authorUsername;
    private LocalDateTime createdAt;
}
