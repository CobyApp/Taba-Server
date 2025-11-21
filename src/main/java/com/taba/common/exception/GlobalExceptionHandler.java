package com.taba.common.exception;

import com.taba.common.dto.ApiResponse;
import com.taba.common.util.MessageUtil;
import com.taba.common.util.SecurityUtil;
import com.taba.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        
        // 사용자 언어 가져오기
        String language = getCurrentUserLanguage();
        
        // 로컬라이징된 메시지 가져오기
        String localizedMessage = MessageUtil.getMessage(errorCode.getMessageKey(), language);
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getCode(), localizedMessage));
    }
    
    /**
     * 현재 사용자의 언어 설정을 가져옵니다.
     * 사용자가 로그인하지 않은 경우 기본값 "ko"를 반환합니다.
     */
    private String getCurrentUserLanguage() {
        try {
            User currentUser = SecurityUtil.getCurrentUser();
            if (currentUser != null && currentUser.getLanguage() != null) {
                return currentUser.getLanguage();
            }
        } catch (Exception e) {
            // 사용자 정보를 가져올 수 없는 경우 무시
        }
        return "ko"; // 기본값: 한국어
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String language = getCurrentUserLanguage();
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            
            // 메시지가 {key} 형식인 경우 로컬라이징 시도
            if (errorMessage != null && errorMessage.startsWith("{") && errorMessage.endsWith("}")) {
                String messageKey = errorMessage.substring(1, errorMessage.length() - 1);
                try {
                    errorMessage = MessageUtil.getMessage(messageKey, language);
                } catch (Exception ex) {
                    // 메시지를 찾을 수 없는 경우 원본 메시지 사용
                }
            }
            
            errors.put(fieldName, errorMessage);
        });
        log.error("ValidationException: {}", errors);
        
        // validation 에러 details를 응답에 포함
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(errors));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(NoResourceFoundException e) {
        String resourcePath = e.getResourcePath();
        log.warn("Resource not found: {}", resourcePath);
        
        // Swagger UI 관련 리소스는 조용히 처리 (404는 정상)
        if (resourcePath != null && (
                resourcePath.contains("swagger-ui") ||
                resourcePath.contains("webjars") ||
                resourcePath.contains("swagger-resources") ||
                resourcePath.contains("v3/api-docs"))) {
            // Swagger UI 리소스는 조용히 404 반환 (브라우저가 처리)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        
        // 그 외의 경우는 404 반환
        String language = getCurrentUserLanguage();
        String localizedMessage = MessageUtil.getMessage("error.not_found", language);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("NOT_FOUND", localizedMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unexpected error: ", e);
        String language = getCurrentUserLanguage();
        String localizedMessage = MessageUtil.getMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessageKey(), language);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        localizedMessage
                ));
    }
}

