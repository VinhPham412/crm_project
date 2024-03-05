package com.cybersoft.crm04.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/*
  1) Nếu như đã đăng nhập rồi thì không cần đăng nhập lại (bỏ qua trang login) và đá về trang chủ (trang dashboard)
  - B1: Khi đăng nhập thành công thì phải lưu lại thông tin user đã đăng nhập (email & password) -> dùng session or cookie
  - B2: Khi người dùng vào lại trang login thì kiểm tra xem session/cookie lưu trữ thông tin người dùng có đang tồn tại hay không
  - B3: Nếu tồn tại -> đã đăng nhập thành công -> chuyển hướng về trang chủ
        Nếu không tồn tại -> chưa login thành công -> cho đi tiếp vào trang login
 */
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        System.out.println("Kiem tra LoginFilterPath: " + request.getServletPath()); // lấy đường dẫn mà người dùng đang gọi để kích hoạt filter
        HttpSession session = request.getSession(); // lấy thông tin session

        // Kiểm tra session lưu trữ ở login lúc đăng nhập thành công với attributeName là "email" có tồn tại giá trị hay không
        if (session != null && session.getAttribute("email") != null && !session.getAttribute("email").equals("")) {
            // nếu giá trị thuộc tính "email" có tồn tại -> chuyển hướng đến link /dashboard (đá về trang chủ)
            //response.sendRedirect("/dashboard");
            response.sendRedirect("http://localhost:8080");
        } else {
            // nếu không tồn tại giá trị thuộc tính "email" -> cho đi tiếp vào đường dẫn /login mà client đang gọi (hoặc thoát khỏi filter và đi tiếp)
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
