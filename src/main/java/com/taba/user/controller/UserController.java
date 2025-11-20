package com.taba.user.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.common.util.SecurityUtil;
import com.taba.user.dto.UserDto;
import com.taba.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(@PathVariable String userId) {
        UserDto userDto = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @PathVariable String userId,
            @ModelAttribute UpdateProfileRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.FORBIDDEN);
        }

        UserDto userDto = userService.updateProfile(
                userId,
                request.getNickname(),
                request.getAvatarUrl(),
                profileImage
        );
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @PutMapping("/{userId}/fcm-token")
    public ResponseEntity<ApiResponse<?>> updateFcmToken(
            @PathVariable String userId,
            @RequestBody FcmTokenRequest request) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.FORBIDDEN);
        }

        userService.updateFcmToken(userId, request.getFcmToken());
        return ResponseEntity.ok(ApiResponse.success(null, "FCM 토큰이 등록되었습니다."));
    }

    @DeleteMapping("/{userId}/fcm-token")
    public ResponseEntity<ApiResponse<?>> deleteFcmToken(@PathVariable String userId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.FORBIDDEN);
        }

        userService.deleteFcmToken(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "FCM 토큰이 삭제되었습니다."));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> withdrawUser(@PathVariable String userId) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new com.taba.common.exception.BusinessException(com.taba.common.exception.ErrorCode.FORBIDDEN);
        }

        userService.withdrawUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "회원탈퇴가 완료되었습니다."));
    }

    @lombok.Getter
    @lombok.Setter
    static class UpdateProfileRequest {
        private String nickname;
        private String avatarUrl;
    }

    @lombok.Getter
    @lombok.Setter
    static class FcmTokenRequest {
        @jakarta.validation.constraints.NotBlank
        private String fcmToken;
    }
}

