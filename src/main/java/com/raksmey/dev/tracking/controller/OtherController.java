package com.raksmey.dev.tracking.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OtherController {


    @GetMapping("/other")
    public ResponseEntity<String> sayHello(HttpServletRequest request) {
        log.info("traceparent: {}", request.getHeader("traceparent"));
        log.info("Someone called the /other endpoint");
        return ResponseEntity.ok("other");
    }
}
