package com.flawyless.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController {

    @RequestMapping(name = "/")
    public String index() {
        return "mvc test";
    }
}
