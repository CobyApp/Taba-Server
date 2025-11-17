package com.taba.friendship.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.friendship.dto.BouquetDto;
import com.taba.friendship.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bouquets")
@RequiredArgsConstructor
public class BouquetController {

    private final FriendshipService friendshipService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BouquetDto>>> getBouquets() {
        List<BouquetDto> bouquets = friendshipService.getBouquets();
        return ResponseEntity.ok(ApiResponse.success(bouquets));
    }

    @GetMapping("/{friendId}/letters")
    public ResponseEntity<ApiResponse<Page<?>>> getFriendLetters(
            @PathVariable String friendId,
            @PageableDefault(size = 20) Pageable pageable) {
        // TODO: 친구별 편지 목록 조회 구현
        return ResponseEntity.ok(ApiResponse.success(Page.empty()));
    }

    @PutMapping("/{friendId}/name")
    public ResponseEntity<ApiResponse<BouquetDto>> updateBouquetName(
            @PathVariable String friendId,
            @RequestBody UpdateNameRequest request) {
        friendshipService.updateBouquetName(friendId, request.getBouquetName());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @lombok.Getter
    @lombok.Setter
    static class UpdateNameRequest {
        private String bouquetName;
    }
}

