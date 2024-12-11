package com.geotracker.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    private String details;
    private LocalDateTime timestamp;
    private int status;
}
