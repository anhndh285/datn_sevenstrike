package com.example.datn_sevenstrike.dto.client;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientTimelineDTO {
    private String status; // Label
    private String description;
    private LocalDateTime time;
    private boolean completed;
    private boolean active;
}