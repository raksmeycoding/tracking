package com.raksmey.dev.tracking.service;

import com.raksmey.dev.tracking.dto.UserRspDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class TestTrackingServiceImpl implements TestTrackingService{

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(TestTrackingServiceImpl.class);
    private final RestTemplate restTemplate;

    public TestTrackingServiceImpl(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @Override
    public CompletableFuture<UserRspDto> test () {
            log.info("*** Outside method ***");
        return CompletableFuture.supplyAsync(() -> {
            log.info("*** In async method ***");
            return null;
        });
    }


    @Override
    public CompletableFuture<List<UserRspDto>> getListOfUserAsync() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("*** In async method ***");
            return userService.getSingleUser();
        })
                .thenApplyAsync((pre) -> {
            log.info("*** In async method ***");
           return List.of(pre, userService.getSingleUser());
        })
                .thenApplyAsync((pre) -> {
                log.info("*** In async method ***");
                List<UserRspDto> userRspDtos = new ArrayList<>();
                userRspDtos.add(userService.getSingleUser());
                userRspDtos.add(userService.getSingleUser());
                userRspDtos.add(userService.getSingleUser());
                return userRspDtos;
                });
    }


    @Override
    @Async("async")
    public CompletableFuture<UserRspDto> getExternalUserAsync() {
        return userService.getExternalUser();
    }

    @Override
    @Async("async")
    public CompletableFuture<List<UserRspDto>> getListOfExternalUserAsync() {
        return userService.getExternalUserListV2();
    }
}
