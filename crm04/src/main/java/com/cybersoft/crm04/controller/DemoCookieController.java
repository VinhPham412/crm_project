package com.cybersoft.crm04.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/cookie")
public class DemoCookieController {

    /**
     * - server bắt client tạo cookie thông qua response
     * - server lấy nội dung cookie từ client gửi lên thông qua request
     */

    @GetMapping("")
    public String createCookie(HttpServletResponse response, HttpServletRequest request) {
//        // Tạo cookie
//        Cookie cookie = new Cookie("greeting", "HelloCookie");
//        Cookie cookie1 = new Cookie("username", URLEncoder.encode("Nguyễn Văn A", StandardCharsets.UTF_8));
//        // server bắt client tạo cookie
//        response.addCookie(cookie);
//        response.addCookie(cookie1);

        Cookie[] cookies = request.getCookies(); // lấy toàn bộ danh sách cookie client truyền lên
        for (Cookie cookie : cookies) { // duyệt qua từng cookie
            String name = cookie.getName(); // lấy name cookie đang duyệt
            String value = cookie.getValue(); // lấy value cookie đang duyệt

            if (name.equals("greeting")) {
                System.out.println("Kiểm tra cặp name-value: \"" + name + "\" - \"" + value + "\"");
            }
        }

        return "login";
    }

}
