package com.jzh.lonershub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OtherController {
    @GetMapping("/about")
    public String about() {
        return "/about";
    }
}
