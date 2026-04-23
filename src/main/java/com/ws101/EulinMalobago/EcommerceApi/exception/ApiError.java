package com.ws101.EulinMalobago.EcommerceApi.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a standardized API error response body.
 *
 * @author eulin and malobago bsit -2b
 * Stores the timestamp, HTTP status information, descriptive message, and
 * request path for a failed API request.
 */
@Getter
@AllArgsConstructor
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
