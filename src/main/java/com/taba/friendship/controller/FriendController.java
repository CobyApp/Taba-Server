package com.taba.friendship.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.friendship.service.FriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ApiResponse<?>> addFriend(@Valid @RequestBody InviteRequest request) {
        friendshipService.addFriend(request.getInviteCode());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "친구가 추가되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<com.taba.user.dto.UserDto>>> getFriends() {
        List<com.taba.user.dto.UserDto> friends = friendshipService.getFriends();
        return ResponseEntity.ok(ApiResponse.success(friends));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<?>> deleteFriend(@PathVariable String friendId) {
        friendshipService.deleteFriend(friendId);
        return ResponseEntity.ok(ApiResponse.success("친구 관계가 삭제되었습니다."));
    }

    @lombok.Getter
    @lombok.Setter
    static class InviteRequest {
        @jakarta.validation.constraints.NotBlank
        private String inviteCode;
    }
}

