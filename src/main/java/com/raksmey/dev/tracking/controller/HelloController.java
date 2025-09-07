package com.raksmey.dev.tracking.controller;

import com.raksmey.dev.tracking.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {
    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }


    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        log.info("Someone called the /hello endpoint");
        return ResponseEntity.ok(helloService.sayHello());
    }


    @GetMapping("/bad-call")
    public void badCall() {
        log.info("Someone called the /bad-call endpoint");
        helloService.fakeBadCall();
    }
}
