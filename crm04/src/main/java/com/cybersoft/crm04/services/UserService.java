package com.cybersoft.crm04.services;

import com.cybersoft.crm04.entity.RolesEntity;
import com.cybersoft.crm04.entity.UsersEntity;
import com.cybersoft.crm04.repository.RolesRepository;
import com.cybersoft.crm04.repository.TasksRepository;
import com.cybersoft.crm04.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    public List<UsersEntity> getAllUsers() {
        return usersRepository.findAll();
    }

    public void deleteUserById(int userId) {
        usersRepository.deleteById(userId);
    }

    public Optional<UsersEntity> getOptionalUserById(int userId) {
        return usersRepository.findById(userId);
    }

    public UsersEntity getUsersEntityById(int userId) { // lấy usersEntity theo id
        // Kiểu Optional: có hoặc không có cũng được
        // Optional chứa các method hỗ trợ sẵn giúp kiểm tra giá trị có null hay không để tránh bị lỗi null dữ liệu trong quá trình xử lý
        Optional<UsersEntity> optionalUser = getOptionalUserById(userId);
        UsersEntity usersEntity = null;
        if (optionalUser.isPresent()) { // isPresent() kiểm tra giá trị trong Optional có tồn tại hay không
            usersEntity = optionalUser.get(); // get() giúp huỷ Optional, trả về kiểu dữ liệu thực của biến
        }

        return usersEntity;
    }

    public UsersEntity getUsersEntityByEmail(String email) { // lấy usersEntity theo email
        return usersRepository.findByEmail(email);
    }

//    public List<UsersEntity> getListUsersByRoleName(String roleName) { // lấy danh sách user theo tên role
//        RolesEntity rolesEntity = rolesRepository.findByName(roleName);
//        return usersRepository.findByRolesEntity(rolesEntity);
//    }

    public List<UsersEntity> getAllLeaders() { // trả về danh sách Leader
        RolesEntity roleLeader = rolesRepository.findByName("ROLE_LEADER");
        return usersRepository.findByRolesEntity(roleLeader);
    }

    public boolean isEmailExist(String email) { // method kiểm tra xem email user có tồn tại trong CSDL hay chưa
        List<UsersEntity> listUsers = usersRepository.findAll(); // lấy toàn bộ danh sách users từ database
        boolean isExist = false; // biến flag kiểm tra xem email có tồn tại trong database hay không, mặc định là không (false)
        for (UsersEntity user : listUsers) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                isExist = true; // email user đã tồn tại trong database
                break;
            }
        }
        return isExist;
    }

    // method kiểm tra xem có add user thành công hay không
    public boolean addUserSuccess(String email, String password, String fullname, String username, String phoneNo, RolesEntity rolesEntity) {
        UsersEntity addUserEntity = new UsersEntity();
        addUserEntity.setEmail(email);
        addUserEntity.setPassword(password);
        addUserEntity.setFullName(fullname);
        addUserEntity.setUsername(username);
        addUserEntity.setPhoneNo(phoneNo);
        addUserEntity.setRolesEntity(rolesEntity);

        boolean isAddSuccess = false; // biến flag kiểm tra xem có thêm user thành công hay không, mặc định là không (false)
        try {
            if (!isEmailExist(email)) { // yêu cầu khi thêm thì email phải khác nhau, nếu email không tồn tại thì mới add user
                usersRepository.save(addUserEntity); // không có setId -> add
                isAddSuccess = true; // thêm thành công và không xảy ra lỗi Runtime
            } else {
                System.out.println(email + " đã tồn tại!");
            }
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi thêm dữ liệu: " + e.getMessage());
        }
        return isAddSuccess;
    }

    public boolean updateUserSuccess(UsersEntity usersEntity) { // method kiểm tra xem có update user thành công hay không
        boolean isUpdateSuccess = false; // biến flag kiểm tra xem có update user thành công hay không, mặc định là không (false)
        try {
            usersRepository.save(usersEntity); // nếu usersEntity có setId -> update
            isUpdateSuccess = true; // update thành công và không xảy ra lỗi Runtime
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi update dữ liệu: " + e.getMessage());
        }
        return isUpdateSuccess;
    }

    // method tự động điền email và password nếu có lưu tài khoản
    public void autofillEmailAndPassword(Model model, HttpServletRequest request) {
        // Khi người dùng vô lại link /login với method GET thì sẽ điền sẵn email và password vào input
        // nếu đã login thành công trước đó và có tích vào checkbox lưu tài khoản bên giao diện login
        Cookie[] cookies = request.getCookies(); // lấy toàn bộ danh sách cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();

                // nếu tồn tại cookie với name là email hoặc password thì lưu giá trị của nó vào model để điền sẵn giá trị bên file html
                if (name.equals("email") && value != null) {
                    model.addAttribute("email", value);
                }
                if (name.equals("password") && value != null) {
                    model.addAttribute("password", value);
                }
            }
        }
    }

    /**
     * Method xử lý chức năng Login:
     * - B1: gọi method findByEmailAndPassword(email,password) bên repo để lấy list user ứng với email và password đó
     * - B2: Kiểm tra xem list user có dữ liệu hay không?
     * - B3: Nếu có (đăng nhập thành công) thì chuyển qua trang dashboard (Lưu tạo link /dashboard sử dụng page index.html)
     *      và khi user đăng xuất thì điền sẵn email và password nếu có tích vào checkbox "Lưu tài khoản"
     * - B4: Nếu thất bại thì xuất thông báo "Đăng nhập thất bại" ra màn hình login
     */
    public String progressLogin(HttpSession httpSession, HttpServletResponse response,
                                Model model, boolean remember, String email, String password) {
        List<UsersEntity> listUser = usersRepository.findByEmailAndPassword(email, password);
        // nếu listUser rỗng (không có phần tử nào) -> đăng nhập thất bại -> isSuccess = false
        // nếu listUser không rỗng (có phần tử) -> đăng nhập thành công -> isSuccess = true
        boolean isSuccess = !listUser.isEmpty();
        // Đẩy giá trị của biến isSuccess trong java ra file HTML và đặt tên key (biến) giữ giá trị đó là "isSuccess"
        model.addAttribute("isSuccess", isSuccess);
        System.out.println("Kiểm tra link /login với phương thức post: " + email + " - " + password);

        if (!isSuccess) { // đăng nhập không thành công -> hiển thị file 'login.html' và xuất thông báo login thất bại
            return "login";
        } else { // đăng nhập thành công -> chuyển hướng đến link /dashboard và hiển thị page 'index.html'
            // tạo session để lưu thông tin user khi login thành công
            httpSession.setAttribute("email", email); // lưu email với attributeName (key) là "email" và value là email mà người dùng đã nhập
            String roleName = listUser.get(0).getRolesEntity().getName(); // lấy tên quyền của user khi login thành công
            httpSession.setAttribute("role", roleName); // lưu tên quyền của user với key là "role" và giá trị là roleName
            httpSession.setMaxInactiveInterval(8 * 60 * 60); // thiết lập thời gian sống cho session là 8h

            // nếu login thành công và client có tích vào checkbox lưu tài khoản (tham số remember = true)
            // -> tạo 2 Cookie để lưu thông tin email và password mà người dùng nhập vào trên browser
            if (remember) {
                Cookie emailCookie = new Cookie("email", email);
                Cookie passwordCookie = new Cookie("password", password);
                response.addCookie(emailCookie);
                response.addCookie(passwordCookie);
            } else { // nếu không tích vào lưu tài khoản thì xoá 2 cookie với tên là email và password nếu đã tạo
                // Tạo 2 cookie với cùng tên và đặt thời gian sống là 0 (để xoá cookie)
                Cookie emailCookieToDelete = new Cookie("email", null);
                Cookie passwordCookieToDelete = new Cookie("password", null);
                emailCookieToDelete.setMaxAge(0);
                passwordCookieToDelete.setMaxAge(0);

                // Đặt đường dẫn và domain nếu có
                emailCookieToDelete.setPath("/"); // Đặt path root để đảm bảo xoá đúng cookie
                passwordCookieToDelete.setPath("/"); // Đặt path root để đảm bảo xoá đúng cookie

                // Thêm cookie vào response để gửi về client
                response.addCookie(emailCookieToDelete);
                response.addCookie(passwordCookieToDelete);
            }

            return "redirect:/dashboard";
        }
    }

}
