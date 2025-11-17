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
                request.getStatusMessage(),
                request.getAvatarUrl(),
                profileImage
        );
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @lombok.Getter
    @lombok.Setter
    static class UpdateProfileRequest {
        private String nickname;
        private String statusMessage;
        private String avatarUrl;
    }
}

