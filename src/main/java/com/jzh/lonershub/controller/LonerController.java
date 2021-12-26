package com.jzh.lonershub.controller;

import com.jzh.lonershub.bean.Loner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class LonerController {
    @GetMapping(value = {"/index", "/"})
    public String index() {
        return "/index";
    }

    @GetMapping(value = "/toLogin")
    public String toLogin() {
        return "/login";
    }

    @GetMapping(value = "/toRegister")
    public String toRegister() {
        return "/register";
    }

    @PostMapping(value = "/register")
    public String register(@RequestParam String lonerName,
                           @RequestParam String lonerPassword,
                           @RequestParam String lonerEmail,
                           @RequestParam String lonerResidence,
                           @RequestPart MultipartFile lonerAvatar) {

        return "/index";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam String lonerEmail,
                        @RequestParam String lonerPassword) {

        return "/index";
    }
}
