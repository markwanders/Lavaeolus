package com.example.lavaeolus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/")
    String index() {
        return "index.html";
    }

    @RequestMapping("/accounts")
    String accounts() {
        return "index.html";
    }

    @RequestMapping({"/transactions/*/*", "/transactions/*", "/transactions"})
    String transactions() {
        return "/index.html";
    }

    @RequestMapping("/user")
    String user() {
        return "index.html";
    }
}
