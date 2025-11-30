package com.example.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/api/demo/hello")
    public String hello() {
        return "Hello from generated backend!";
    }
}