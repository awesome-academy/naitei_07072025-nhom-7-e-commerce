package com.group7.ecommerce.controller.admin;

import com.group7.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping("/home")
    public String admin(){
        log.info("Accessing admin home page");
        return "admin/index";
    }


}