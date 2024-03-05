package com.cybersoft.crm04.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * AuthenticationFilter kiểm tra xem link mà client đang gọi đã đăng nhập hay chưa (nếu chưa thì đá về trang login, ngược lại thì đi tiếp)
 */
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();
        System.out.println("Kiem tra AuthenticationFilterPath: " + path); // lấy đường dẫn mà người dùng đang gọi để kích hoạt filter
        HttpSession session = request.getSession();

        if (session != null && session.getAttribute("email") != null && !session.getAttribute("email").equals("")) {
            // nếu đã login thành công -> đi tiếp
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // nếu giá trị thuộc tính "email" không tồn tại hoặc bằng rỗng -> chưa login hoặc login thất bại -> đá về trang login
            response.sendRedirect("/login");
        }
    }
}
