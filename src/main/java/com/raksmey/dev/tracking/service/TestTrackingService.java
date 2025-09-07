package com.raksmey.dev.tracking.service;

import com.raksmey.dev.tracking.dto.UserRspDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TestTrackingService {

    CompletableFuture<UserRspDto> test ();

    CompletableFuture<List<UserRspDto>> getListOfUserAsync();

    CompletableFuture<UserRspDto> getExternalUserAsync();

    CompletableFuture<List<UserRspDto>> getListOfExternalUserAsync();
}
