package com.raksmey.dev.tracking.controller;


import com.raksmey.dev.tracking.core.ApiRespond;
import com.raksmey.dev.tracking.dto.UserRspDto;
import com.raksmey.dev.tracking.service.TestTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/tracking/${api.version}/")
public class TestTrackingController {

    private final TestTrackingService testTrackingService;

    public TestTrackingController(TestTrackingService testTrackingService) {
        this.testTrackingService = testTrackingService;
    }

    @PostMapping("/user")
    public CompletableFuture<ResponseEntity<ApiRespond<UserRspDto>>> getAUser() {
        log.info("Micrometer Tracing is working!");

        return testTrackingService
                .test()
                .thenApply(userRspDto -> ResponseEntity.ok(
                        ApiRespond.<UserRspDto>builder()
                                .message("Success")
                                .data(userRspDto)
                                .build()
                ));
    }

    @PostMapping("/users")
    public CompletableFuture<ResponseEntity<ApiRespond<List<UserRspDto>>>> getListOfUsers() {
        log.info("Micrometer Tracing is working!");
        var result = testTrackingService.test();

        return testTrackingService.getListOfUserAsync()
                .thenApplyAsync(listOfUsers -> ResponseEntity.ok(
                        ApiRespond.<List<UserRspDto>>builder()
                                .message("Success")
                                .data(listOfUsers)
                                .build()
                ));
    }

    @PostMapping("/external-users/single")
    public CompletableFuture<ResponseEntity<ApiRespond<UserRspDto>>> single() {
        log.info("Micrometer Tracing is working!");

        return testTrackingService.getExternalUserAsync()
                .thenApplyAsync(listOfUsers -> ResponseEntity.ok(
                        ApiRespond.<UserRspDto>builder()
                                .message("Success")
                                .data(listOfUsers)
                                .build()
                ));
    }

    @PostMapping("/external-users/list")
    public CompletableFuture<ResponseEntity<ApiRespond<List<UserRspDto>>>> getExternalUserList() {
        log.info("Micrometer Tracing is working!");

        return testTrackingService.getListOfExternalUserAsync()
                .thenApplyAsync(listOfUsers -> ResponseEntity.ok(
                        ApiRespond.<List<UserRspDto>>builder()
                                .message("Success")
                                .data(listOfUsers)
                                .build()
                ));
    }
}
