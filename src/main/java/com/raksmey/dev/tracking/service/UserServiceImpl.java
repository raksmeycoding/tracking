package com.raksmey.dev.tracking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raksmey.dev.tracking.core.JsonResponse;
import com.raksmey.dev.tracking.dto.UserRspDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final RestTemplate restTemplate;

    private final Executor asyncExecutor;

    public UserServiceImpl(RestTemplate restTemplate, @Qualifier("async") Executor asyncExecutor) {
        this.restTemplate = restTemplate;
        this.asyncExecutor = asyncExecutor;
    }

    public UserRspDto getSingleUser() {
        logger.info("*** Mock a single user ***");
        return null;
    }

    @Override
    @Async("async")
    public CompletableFuture<UserRspDto> getExternalUser() {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:9090/api/v1.0.0/users/generate-user",
                    null,
                    String.class
            );
            logger.info("response: {}", response);

            String responseBody = response.getBody();
            logger.info("responseBody: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse<UserRspDto> userRspDtoJsonResponse = objectMapper.readValue(
                    responseBody,
                    new TypeReference<>() {}
            );

            return CompletableFuture.completedFuture(userRspDtoJsonResponse.getData());

        } catch (Exception e) {
            logger.error("Failed to fetch external user", e);
            return CompletableFuture.failedFuture(e);
        }
    }


    @Override
    @Async("async")
    public CompletableFuture<List<UserRspDto>> getExternalUserList() {
        try {

            // Call getExternalUserSingle() three times asynchronously
            CompletableFuture<UserRspDto> future1 = getExternalUserSingle();
            CompletableFuture<UserRspDto> future2 = getExternalUserSingle();
            CompletableFuture<UserRspDto> future3 = getExternalUserSingle();

            // Combine all futures into one
            return CompletableFuture.allOf(future1, future2, future3)
                    .thenApply(voidResult -> {
                        try {
                            return List.of(future1.get(), future2.get(), future3.get());
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to combine user responses", e);
                        }
                    });

        } catch (Exception e) {
            logger.error("Failed to fetch external user", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Async("async")
    public CompletableFuture<List<UserRspDto>> getExternalUserListV2() {
        CompletableFuture<UserRspDto> f1 = CompletableFuture.supplyAsync(this::fetchUser, asyncExecutor);
        CompletableFuture<UserRspDto> f2 = CompletableFuture.supplyAsync(this::fetchUser, asyncExecutor);
        CompletableFuture<UserRspDto> f3 = CompletableFuture.supplyAsync(this::fetchUser, asyncExecutor);

        return CompletableFuture.allOf(f1, f2, f3)
                .thenApply(v -> List.of(f1.join(), f2.join(), f3.join()));
    }

    @Async("async")
    public CompletableFuture<UserRspDto> getExternalUserSingle() {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:9090/api/v1.0.0/users/generate-user",
                    null,
                    String.class
            );
            logger.info("response: {}", response);

            String responseBody = response.getBody();
            logger.info("responseBody: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse<UserRspDto> userRspDtoJsonResponse = objectMapper.readValue(
                    responseBody,
                    new TypeReference<>() {}
            );
            return CompletableFuture.completedFuture(userRspDtoJsonResponse.getData());
        } catch (Exception e) {
            logger.error("Failed to fetch external user", e);
            throw new RuntimeException("Failed to fetch external user", e);
        }
    }

    private UserRspDto fetchUser() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:9090/api/v1.0.0/users/generate-user",
                null,
                String.class
        );
        logger.info("response: {}", response);
        String responseBody = response.getBody();
        logger.info("responseBody: {}", responseBody);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonResponse<UserRspDto> parsed = mapper.readValue(response.getBody(), new TypeReference<>() {});
            return parsed.getData();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response", e);
        }
    }


}
