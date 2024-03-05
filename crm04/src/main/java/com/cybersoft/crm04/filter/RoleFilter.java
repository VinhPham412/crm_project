package com.cybersoft.crm04.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * - Nếu vào trang add role (link /role/add) mà chưa đăng nhập thì chuyển về trang đăng nhập
 * - Nếu vào trang add role (link /role/add) mà đã đăng nhập rồi thì có 2 TH:
 * + Nếu có quyền ADMIN thì mới cho vào
 * + Ngược lại nếu không có quyền ADMIN thì chuyển về trang 404.html
 */
public class RoleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();
        System.out.println("Kiem tra RoleFilterPath: " + path); // lấy đường dẫn mà người dùng đang gọi để kích hoạt filter
        HttpSession session = request.getSession();

        // nếu đã login thành công (qua AuthenticationFilter) -> kiểm tra đường dẫn mà người dùng đang gọi
        if (path.startsWith("/role/add") || path.startsWith("/role/delete") || path.startsWith("/role/update")
                || path.startsWith("/user/add") || path.startsWith("/user/delete") || path.startsWith("/user/update")
                || path.startsWith("/job/add") || path.startsWith("/job/delete") || path.startsWith("/job/update")) {
            // chỉ có quyền ADMIN thì mới được phép thêm, xóa, sửa role, user và job
            if (session.getAttribute("role").equals("ROLE_ADMIN")) { // kiểm tra quyền ADMIN thì mới cho vào
                filterChain.doFilter(servletRequest, servletResponse);
            } else { // không có quyền ADMIN thì chuyển về link /404 (trang 404.html)
                response.sendRedirect("/404");
            }
        } else if (path.startsWith("/task/add") || path.startsWith("/task/delete") || path.startsWith("/task/update")) {
            // chỉ có quyền ADMIN hoặc LEADER thì mới được phép thêm, xóa, sửa task
            if (session.getAttribute("role").equals("ROLE_ADMIN") || session.getAttribute("role").equals("ROLE_LEADER")) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else { // không có quyền ADMIN hoặc LEADER thì chuyển về link /404 (trang 404.html)
                response.sendRedirect("/404");
            }
        }
        else {
            // bỏ qua filter và tiếp tục chuyển hướng đến link mà client đang gọi
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
