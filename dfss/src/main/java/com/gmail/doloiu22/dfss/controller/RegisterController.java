package com.gmail.doloiu22.dfss.controller;

import com.gmail.doloiu22.dfss.model.UserEntity;
import com.gmail.doloiu22.dfss.service.UserService;
import com.gmail.doloiu22.dfss.service.UserValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private UserService userService;
    private UserValidatorService userValidatorService;

    @Autowired
    public RegisterController(UserService userService, UserValidatorService userValidatorService) {
        this.userService = userService;
        this.userValidatorService = userValidatorService;
    }

    @GetMapping()
    public String open(Model model){
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    @PostMapping()
    public String register(@RequestParam("username") String username,
                           @ModelAttribute("user") UserEntity user, BindingResult bindingResult){

        userValidatorService.validate(user, bindingResult);

        if(bindingResult.hasErrors())
            return "error";

        if (userService.register(user))
            return "/login";
        else return "error";
    }
}