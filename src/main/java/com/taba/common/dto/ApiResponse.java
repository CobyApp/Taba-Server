package com.taba.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Boolean success;
    private T data;
    private String message;
    private ErrorResponse error;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorResponse.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
        private Object details;
    }
    
    public static <T> ApiResponse<T> validationError(Map<String, String> errors) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("입력값 검증에 실패했습니다.")
                .details(errors)
                .build();
        
        return ApiResponse.<T>builder()
                .success(false)
                .error(errorResponse)
                .build();
    }
}

