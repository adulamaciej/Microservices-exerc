package org.example.orderservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String title;
    private String message;
    private LocalDateTime timestamp;

}
