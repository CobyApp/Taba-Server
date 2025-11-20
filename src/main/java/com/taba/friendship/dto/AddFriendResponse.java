package com.taba.friendship.dto;

import com.taba.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendResponse {
    private UserDto friend;
    private boolean alreadyFriends;
    private boolean isOwnCode;
}

