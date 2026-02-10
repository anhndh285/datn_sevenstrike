package com.example.datn_sevenstrike.exception;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {
    private Integer status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
