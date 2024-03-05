package com.cybersoft.crm04.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logout")
public class LogoutController {

    @GetMapping("")
    public String logout(HttpServletRequest request) {
        // Xóa session
        request.getSession().invalidate();

        // Chuyển hướng người dùng về trang login
        return "redirect:/login";
    }

}
