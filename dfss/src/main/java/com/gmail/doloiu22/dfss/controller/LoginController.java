package com.gmail.doloiu22.dfss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String open(){
        return "login";
    }

}