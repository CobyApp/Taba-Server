package com.taba.friendship.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.common.util.MessageUtil;
import com.taba.common.util.SecurityUtil;
import com.taba.friendship.service.FriendshipService;
import com.taba.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendshipService friendshipService;

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<com.taba.friendship.dto.AddFriendResponse>> addFriend(@Valid @RequestBody InviteRequest request) {
        com.taba.friendship.dto.AddFriendResponse response = friendshipService.addFriend(request.getInviteCode());
        
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        
        String message;
        if (response.isOwnCode()) {
            message = MessageUtil.getMessage("api.friend.cannot_add_self", language);
        } else if (response.isAlreadyFriends()) {
            message = MessageUtil.getMessage("api.friend.already_friends", language);
        } else {
            message = MessageUtil.getMessage("api.friend.added", language);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, message));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<com.taba.user.dto.UserDto>>> getFriends() {
        List<com.taba.user.dto.UserDto> friends = friendshipService.getFriends();
        return ResponseEntity.ok(ApiResponse.success(friends));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<?>> deleteFriend(@PathVariable String friendId) {
        friendshipService.deleteFriend(friendId);
        User currentUser = SecurityUtil.getCurrentUser();
        String language = currentUser != null && currentUser.getLanguage() != null ? currentUser.getLanguage() : "ko";
        String message = MessageUtil.getMessage("api.friend.deleted", language);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/{friendId}/letters")
    public ResponseEntity<ApiResponse<Page<com.taba.friendship.dto.SharedFlowerDto>>> getFriendLetters(
            @PathVariable String friendId,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<com.taba.friendship.dto.SharedFlowerDto> letters = friendshipService.getFriendLetters(friendId, pageable);
        return ResponseEntity.ok(ApiResponse.success(letters));
    }

    @lombok.Getter
    @lombok.Setter
    static class InviteRequest {
        @jakarta.validation.constraints.NotBlank
        private String inviteCode;
    }
}

