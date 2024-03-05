package com.cybersoft.crm04.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/404")
public class NotFoundController {

    @GetMapping("")
    public String show404Page() {
        return "404";
    }

}
