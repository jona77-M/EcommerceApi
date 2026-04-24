package com.ws101.EulinMalobago.EcommerceApi.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.dao.DataIntegrityViolationException;
import jakarta.persistence.EntityNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provides centralized exception handling for the application's REST API.
 *
 * @author eulin and malobago bsit -2b
 *         Converts common exceptions into consistent JSON error responses for
 *         404 Not Found, 400 Bad Request, and 500 Internal Server Error cases.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Handles missing product errors.
         *
         * @param exception the thrown not found exception
         * @param request   the current HTTP request
         * @return a 404 response containing a standardized error body
         */
        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ApiError> handleProductNotFound(
                        ProductNotFoundException exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(buildApiError(HttpStatus.NOT_FOUND, exception.getMessage(),
                                                request.getRequestURI()));
        }

        /**
         * Handles invalid input and validation errors from service logic.
         *
         * @param exception the thrown bad request exception
         * @param request   the current HTTP request
         * @return a 400 response containing a standardized error body
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiError> handleBadRequest(
                        IllegalArgumentException exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(buildApiError(HttpStatus.BAD_REQUEST, exception.getMessage(),
                                                request.getRequestURI()));
        }

        /**
         * Handles bean validation errors from @Valid.
         *
         * @param exception the validation exception
         * @return a 400 response containing field-specific validation messages
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationErrors(
                        MethodArgumentNotValidException exception) {

                Map<String, String> errors = new HashMap<>();

                exception.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest().body(errors);
        }

        /**
         * Handles unexpected server-side errors.
         *
         * @param exception the unexpected exception
         * @param request   the current HTTP request
         * @return a 500 response containing a standardized error body
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleServerError(
                        Exception exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(buildApiError(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "An unexpected server error occurred.",
                                                request.getRequestURI()));
        }

        /**
         * Builds a reusable API error payload.
         *
         * @param status  the HTTP status to return
         * @param message the error message for the response body
         * @param path    the request path where the error occurred
         * @return the populated API error object
         */
        private ApiError buildApiError(HttpStatus status, String message, String path) {
                return new ApiError(
                                LocalDateTime.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                path);
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ApiError> handleEntityNotFound(
                        EntityNotFoundException exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(buildApiError(HttpStatus.NOT_FOUND, exception.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiError> handleDataIntegrityViolation(
                        DataIntegrityViolationException exception,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(buildApiError(
                                                HttpStatus.BAD_REQUEST,
                                                "Database constraint violation: "
                                                                + exception.getMostSpecificCause().getMessage(),
                                                request.getRequestURI()));
        }

}

// package com.ws101.EulinMalobago.EcommerceApi.exception;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;
// import org.springframework.web.bind.MethodArgumentNotValidException;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// import jakarta.servlet.http.HttpServletRequest;

// /**
// * Provides centralized exception handling for the application's REST API.
// *
// * @author eulin and malobago bsit -2b
// * Converts common exceptions into consistent JSON error responses for
// * 404 Not
// * Found, 400 Bad Request, and 500 Internal Server Error cases.
// */
// @RestControllerAdvice
// public class GlobalExceptionHandler {

// /**
// * Handles missing product errors.
// *
// * @param exception the thrown not found exception
// * @param request the current HTTP request
// * @return a 404 response containing a standardized error body
// */
// @ExceptionHandler(ProductNotFoundException.class)
// public ResponseEntity<ApiError> handleProductNotFound(
// ProductNotFoundException exception,
// HttpServletRequest request) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND)
// .body(buildApiError(HttpStatus.NOT_FOUND, exception.getMessage(),
// request.getRequestURI()));
// }

// /**
// * Handles invalid input and validation errors.
// *
// * @param exception the thrown bad request exception
// * @param request the current HTTP request
// * @return a 400 response containing a standardized error body
// */
// @ExceptionHandler(IllegalArgumentException.class)
// public ResponseEntity<ApiError> handleBadRequest(
// IllegalArgumentException exception,
// HttpServletRequest request) {
// return ResponseEntity.status(HttpStatus.BAD_REQUEST)
// .body(buildApiError(HttpStatus.BAD_REQUEST, exception.getMessage(),
// request.getRequestURI()));
// }

// /**
// * Handles unexpected server-side errors.
// *
// * @param exception the unexpected exception
// * @param request the current HTTP request
// * @return a 500 response containing a standardized error body
// */
// @ExceptionHandler(Exception.class)
// public ResponseEntity<ApiError> handleServerError(
// Exception exception,
// HttpServletRequest request) {
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
// .body(buildApiError(
// HttpStatus.INTERNAL_SERVER_ERROR,
// "An unexpected server error occurred.",
// request.getRequestURI()));
// }

// @ExceptionHandler(MethodArgumentNotValidException.class)
// public ResponseEntity<Map<String, String>>
// handleValidationErrors(MethodArgumentNotValidException ex) {

// Map<String, String> errors = new HashMap<>();

// ex.getBindingResult().getFieldErrors()
// .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

// return ResponseEntity.badRequest().body(errors);
// }

// /**
// * Builds a reusable API error payload.
// *
// * @param status the HTTP status to return
// * @param message the error message for the response body
// * @param path the request path where the error occurred
// * @return the populated API error object
// */
// private ApiError buildApiError(HttpStatus status, String message, String
// path) {
// return new ApiError(
// LocalDateTime.now(),
// status.value(),
// status.getReasonPhrase(),
// message,
// path);
// }
// }
