package com.dnd.modutime.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/sample")
    public String greeting() {
        return "sample~";
    }
}
