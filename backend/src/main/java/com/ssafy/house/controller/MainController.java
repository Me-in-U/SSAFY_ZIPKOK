package com.ssafy.house.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "https://api.ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/" })
public class MainController {
    @GetMapping({ "/", "/index" })
    public String index() {
        return "index";
    }
}
