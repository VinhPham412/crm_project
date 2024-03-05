package com.cybersoft.crm04.controller;

import com.cybersoft.crm04.entity.RolesEntity;
import com.cybersoft.crm04.entity.StatusEntity;
import com.cybersoft.crm04.entity.TasksEntity;
import com.cybersoft.crm04.entity.UsersEntity;
import com.cybersoft.crm04.services.RoleService;
import com.cybersoft.crm04.services.StatusService;
import com.cybersoft.crm04.services.TaskService;
import com.cybersoft.crm04.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private StatusService statusService;

    @GetMapping("/profile")
    public String showProfile(Model model, HttpServletRequest request) {
        String email = request.getSession().getAttribute("email").toString();
        UsersEntity usersEntity = userService.getUsersEntityByEmail(email);
        model.addAttribute("usersEntity", usersEntity);
        taskService.progressShowTasks(model, request); // add danh sách công việc của user đó vào model để hiển thị lên giao diện profile
        int percentageOfUnimplementedTask = taskService.getThePercentageOfUnimplementedTaskByUser(usersEntity);
        int percentageOfInProgressTask = taskService.getThePercentageOfInProgressTaskByUser(usersEntity);
        int percentageOfCompletedTask = taskService.getThePercentageOfCompletedTaskByUser(usersEntity);
        model.addAttribute("percentageOfUnimplementedTask", percentageOfUnimplementedTask);
        model.addAttribute("percentageOfInProgressTask", percentageOfInProgressTask);
        model.addAttribute("percentageOfCompletedTask", percentageOfCompletedTask);

        return "profile";
    }

    // profile-edit: cập nhật tiến độ công việc (status)
    @GetMapping("/profile/edit/{taskId}")
    public String showProfileEdit(Model model, @PathVariable int taskId) {
        Optional<TasksEntity> optionalTasksEntity = taskService.getOptionalTaskById(taskId);
        optionalTasksEntity.ifPresent(value -> {
            model.addAttribute("updateTaskEntity", value);
        });
        List<StatusEntity> listStatus = statusService.getAllStatus();
        model.addAttribute("listStatus", listStatus);

        return "profile-edit";
    }

    @PostMapping("/profile/edit/{taskId}")
    public String progressProfileEdit(Model model, @PathVariable int taskId, @RequestParam("selectedStatusId") int statusId) {
        TasksEntity updateTaskEntity = taskService.getTasksEntityById(taskId);
        updateTaskEntity.setId(taskId); // có setId -> update
        StatusEntity statusEntity = statusService.getStatusEntityById(statusId);
        updateTaskEntity.setStatusEntity(statusEntity);
        boolean isUpdateSuccess = taskService.updateTaskSuccess(updateTaskEntity);
        model.addAttribute("isUpdateSuccess", isUpdateSuccess); // thêm isUpdateSuccess vào model để hiển thị ra giao diện thành công hay thất bại
        model.addAttribute("updateTaskEntity", updateTaskEntity); // thêm updateTaskEntity vào model để tránh lỗi NullPointerException
        List<StatusEntity> listStatus = statusService.getAllStatus();
        model.addAttribute("listStatus", listStatus);

        return "profile-edit";
    }

    @GetMapping("/show")
    public String showUserTable(Model model) { // show danh sách thành viên
        List<UsersEntity> listUsers = userService.getAllUsers();
        model.addAttribute("listUsers", listUsers);

        return "user-table";
    }

    @GetMapping("/details/{userId}")
    public String showUserDetails(Model model, @PathVariable int userId) { // show chi tiết thành viên
        UsersEntity usersEntity = userService.getUsersEntityById(userId);
        model.addAttribute("usersEntity", usersEntity);
        int percentageOfUnimplementedTask = taskService.getThePercentageOfUnimplementedTaskByUser(usersEntity);
        int percentageOfInProgressTask = taskService.getThePercentageOfInProgressTaskByUser(usersEntity);
        int percentageOfCompletedTask = taskService.getThePercentageOfCompletedTaskByUser(usersEntity);
        model.addAttribute("percentageOfUnimplementedTask", percentageOfUnimplementedTask);
        model.addAttribute("percentageOfInProgressTask", percentageOfInProgressTask);
        model.addAttribute("percentageOfCompletedTask", percentageOfCompletedTask);
        model.addAttribute("listOfUnimplementedTasks", taskService.getListOfUnimplementedTasks(usersEntity));
        model.addAttribute("listOfInProgressTasks", taskService.getListOfInProgressTasks(usersEntity));
        model.addAttribute("listOfCompletedTasks", taskService.getListOfCompletedTasks(usersEntity));

        return "user-details";
    }

    @GetMapping("/add")
    public String addUser(Model model) {
        List<RolesEntity> listRoles = roleService.getAllRoles();
        model.addAttribute("listRoles", listRoles);

        return "user-add";
    }

    @PostMapping("/add")
    public String progressAddUser(Model model, @RequestParam String email, @RequestParam String password,
                                  @RequestParam String fullname, @RequestParam String username,
                                  @RequestParam("phone-no") String phoneNo, @RequestParam("selectedRoleId") int roleId) {
        RolesEntity rolesEntity = roleService.getRolesEntityById(roleId);
        boolean isAddUserSuccess = userService.addUserSuccess(email, password, fullname, username, phoneNo, rolesEntity);
        model.addAttribute("isAddUserSuccess", isAddUserSuccess);
        // điền sẵn các thông tin mà người dùng đã nhập trong thẻ form sau khi click button Add User
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("fullname", fullname);
        model.addAttribute("username", username);
        model.addAttribute("phoneNo", phoneNo);
        List<RolesEntity> listRoles = roleService.getAllRoles();
        model.addAttribute("listRoles", listRoles);

        return "user-add";
    }

    @GetMapping("/delete/{userId}")
    public String progressDeleteUserById(@PathVariable(name = "userId") int id) { // Phương thức xử lý xoá user theo id
        userService.deleteUserById(id);

        return "redirect:/user/show";
    }

    @GetMapping("/update/{userId}")
    public String showUpdateUser(Model model, @PathVariable int userId) {
        Optional<UsersEntity> optionalUsersEntity = userService.getOptionalUserById(userId);
        optionalUsersEntity.ifPresent(value -> {
            model.addAttribute("updateUserEntity", value);
        });
        List<RolesEntity> listRoles = roleService.getAllRoles();
        model.addAttribute("listRoles", listRoles);

        return "user-update";
    }

    @PostMapping("/update/{userId}")
    public String progressUpdateUser(Model model, @PathVariable int userId, @RequestParam String email, @RequestParam String password,
                                     @RequestParam String fullname, @RequestParam String username,
                                     @RequestParam("phone-no") String phoneNo, @RequestParam("selectedRoleId") int roleId) {
        // Khởi tạo 1 đối tượng UsersEntity và gán giá trị cho các thuộc tính của đối tượng đó (có setId -> update)
        UsersEntity updateUserEntity = new UsersEntity();
        updateUserEntity.setId(userId); // có setId -> update
        updateUserEntity.setEmail(email);
        updateUserEntity.setPassword(password);
        updateUserEntity.setFullName(fullname);
        updateUserEntity.setUsername(username);
        updateUserEntity.setPhoneNo(phoneNo);
        RolesEntity rolesEntity = roleService.getRolesEntityById(roleId);
        updateUserEntity.setRolesEntity(rolesEntity);
        boolean isUpdateSuccess = userService.updateUserSuccess(updateUserEntity);
        model.addAttribute("isUpdateSuccess", isUpdateSuccess); // thêm isUpdateSuccess vào model để hiển thị ra giao diện thành công hay thất bại
        model.addAttribute("updateUserEntity", updateUserEntity); // thêm updateUserEntity vào model để tránh lỗi NullPointerException
        List<RolesEntity> listRoles = roleService.getAllRoles();
        model.addAttribute("listRoles", listRoles);

        return "user-update";
    }

}
