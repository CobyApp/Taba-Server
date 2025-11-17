package com.taba.invite.controller;

import com.taba.common.dto.ApiResponse;
import com.taba.invite.dto.InviteCodeDto;
import com.taba.invite.service.InviteCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invite-codes")
@RequiredArgsConstructor
public class InviteCodeController {

    private final InviteCodeService inviteCodeService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<InviteCodeDto>> generateInviteCode() {
        InviteCodeDto inviteCode = inviteCodeService.generateInviteCode();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(inviteCode));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<InviteCodeDto>> getCurrentInviteCode() {
        InviteCodeDto inviteCode = inviteCodeService.getCurrentInviteCode();
        return ResponseEntity.ok(ApiResponse.success(inviteCode));
    }
}

