package com.raksmey.dev.tracking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class HelloService {
    private final RestTemplate restTemplate;
    private final Executor asyncExecutor;

    public HelloService(RestTemplate restTemplate, @Qualifier("async") Executor asyncExecutor) {
        this.restTemplate = restTemplate;
        this.asyncExecutor = asyncExecutor;
    }

    public String sayHello() {
        log.info("Returning hello from service");
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/other", String.class);
            return response.getBody();
        }, asyncExecutor);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/other", String.class);
            return response.getBody();
        }, asyncExecutor);

        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error(throwable.getMessage(), throwable);
            }
            log.info("Got a response from the service");
            log.info("respond body: {}", result);
        });

        future2.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error(throwable.getMessage(), throwable);
            }
            log.info("Got a response from the service");
            log.info("respond body: {}", result);
        });
        return "hello";
    }

//    public void hello(){
//        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9090/api/v1.0.0/users/generate-user", String.class);
//        log.info("response: {}", response.getBody());
//        log.info("Hello from HelloService");
//    }

    public void fakeBadCall() {
        log.info("About to throw IllegalArgumentException...");
        throw new IllegalArgumentException("Exception from Hello World Service");
    }
}
