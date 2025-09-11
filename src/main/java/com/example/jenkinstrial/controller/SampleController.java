package com.example.jenkinstrial.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    @RequestMapping("/jenkins")
    @ResponseBody
    public String welcome() {
        return "Jenkins";
    }

    @RequestMapping("/notestcoverage")
    @ResponseBody
    public String noTestCoverage(){
        String testData = "This is the test Data";
        if (testData.length()>0){
            return"length greater than 0";
        }else{
            return "length less than 0";
        }
    }


    @RequestMapping("/codeduplication")
    @ResponseBody
    public String codeDuplication() {
        String testData = "This is the test Data";
        if (testData.length()>0){
            return"length greater than 0";
        }else{
            return "length less than 0";
        }
    }

}