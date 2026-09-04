package com.projects.teamsync.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projects.teamsync.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse> handleResourceNotFound(
                        ResourceNotFoundException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse(false, ex.getMessage()));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse> handleBadRequest(
                        BadRequestException ex) {

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse(false, ex.getMessage()));
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse> handleUnauthorized(
                        UnauthorizedException ex) {

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiResponse(false, ex.getMessage()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse> handleAccessDenied(
                        AccessDeniedException ex) {

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(new ApiResponse(false, ex.getMessage()));
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiResponse> handleConflict(
                        ConflictException ex) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(new ApiResponse(
                                                false,
                                                ex.getMessage()));
        }

        @ExceptionHandler(EmailSendingException.class)
        public ResponseEntity<ApiResponse> handleEmailSendingException(
                        EmailSendingException ex) {

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse(
                                                        false,
                                                        ex.getMessage()));
        }
}