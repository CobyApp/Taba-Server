package com.taba.block.controller;

import com.taba.block.dto.BlockedUserDto;
import com.taba.block.service.BlockService;
import com.taba.common.dto.ApiResponse;
import com.taba.common.util.MessageUtil;
import com.taba.common.util.SecurityUtil;
import com.taba.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    /**
     * 사용자 차단
     * POST /blocks/{userId}
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> blockUser(@PathVariable String userId) {
        blockService.blockUser(userId);
        
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.block.blocked", language);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message));
    }

    /**
     * 차단 해제
     * DELETE /blocks/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> unblockUser(@PathVariable String userId) {
        blockService.unblockUser(userId);
        
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.block.unblocked", language);
        
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * 차단한 사용자 목록 조회
     * GET /blocks
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BlockedUserDto>>> getBlockedUsers() {
        List<BlockedUserDto> blockedUsers = blockService.getBlockedUsers();
        return ResponseEntity.ok(ApiResponse.success(blockedUsers));
    }
}

