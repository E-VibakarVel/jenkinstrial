package com.example.jenkinstrial.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SampleController {

    @GetMapping("/jenkins")
    public String welcome() {
        return "Jenkins "; // This refers to a view named "welcome" (e.g., welcome.jsp or welcome.html)
    }
}