package com.cybersoft.crm04.controller;

import com.cybersoft.crm04.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Các bước cần làm cho 1 chức năng trong ứng dụng:
 * - B1: Phân tích yêu cầu chức năng, tức là phân tích chức năng cần làm gì, kết quả mong muốn là gì
 * - B2: Xác định được câu truy vấn (query) đối với chức năng đó
 * - B3: Từ câu truy vấn sẽ xác định được method ứng với đường dẫn mà mình định nghĩa cho chức năng đó
 * có nhận tham số hay không và nhận bao nhiêu tham số?
 */

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String login(Model model, HttpServletRequest request) {
//        List<UsersEntity> listUser = usersRepository.findByEmailAndPassword("nguyenvana@gmail.com", "123456");
//        System.out.print("Kiểm tra link /login với phương thức get: ");
//        for (UsersEntity user : listUser) {
//            System.out.println(user.getEmail() + " - " + user.getPassword());
//        }
        userService.autofillEmailAndPassword(model, request);

        return "login";
    }

    /**
     * BVN buổi 28: Hoàn thiện chức năng Login:
     * - B1: Lấy các giá trị mà người dùng nhập vào form login (trường email & password) làm đối số
     *   rồi truyền vào cho các tham số của method findByEmailAndPassword() để kiểm tra đăng nhập
     *      + Làm thế nào để lấy được tham số mà người dùng nhập vào? (thông qua thẻ form bên file html với method post)
     *      + Bên file html có cách nào để gọi được link /login với method post? (thông qua 2 thuộc tính action và method của thẻ form)
     *      + Làm cách nào để truyền email và password mà người dùng nhập vào thẻ form để xử lý?
     *      -> giá trị thuộc tính name của thẻ input bên form login chính là tên tham số của hàm progressLogin()
     *      và giá trị truyền vào cho tham số đó chính là dữ liệu email & password mà người dùng nhập vào khi click submit button
     * - B2: Kiểm tra xem list có dữ liệu hay không?
     * - B3: Nếu có (đăng nhập thành công) thì chuyển qua trang dashboard (Lưu tạo link /dashboard sử dụng page index.html)
     * - B4: Nếu thất bại thì xuất thông báo "Đăng nhập thất bại" ra màn hình login
     *      + Làm cách nào để trả giá trị của biến trong java ra file html? (Model)
     *      + Làm cách nào để lấy được giá trị của biến trong java hiển thị ra file html? (Thymeleaf)
     * Lưu ý: phương thức post => chỉnh form data bên giao diện login
     */

    // Controller: nơi định nghĩa link
    // Model: cho phép trả giá trị của biến java ra file html (View), Model lưu giữ thông tin dưới dạng key-value
    // View: Chính là các file giao diện html
    @PostMapping("")
    public String progressLogin(HttpSession httpSession, HttpServletResponse response,
                                Model model, @RequestParam(defaultValue = "false") boolean remember,
                                @RequestParam String email, @RequestParam String password) {
        return userService.progressLogin(httpSession, response, model, remember, email, password);
    }
}

/**
 * BVN buổi 30:
 * Khi đăng nhập thành công thì lưu email và password vào Cookie lưu trên trình duyệt
 * Khi người dùng vô lại link /login thì sẽ điền sẵn email và password vào input form login nếu có tích vào checkbox "Lưu tài khoản"
 */