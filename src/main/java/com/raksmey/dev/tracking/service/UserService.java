package com.raksmey.dev.tracking.service;

import com.raksmey.dev.tracking.dto.UserRspDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    UserRspDto getSingleUser();

    CompletableFuture<UserRspDto> getExternalUser();

    CompletableFuture<List<UserRspDto>> getExternalUserList();

    CompletableFuture<List<UserRspDto>> getExternalUserListV2();
}
