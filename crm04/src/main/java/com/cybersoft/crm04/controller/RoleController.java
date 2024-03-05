package com.cybersoft.crm04.controller;

import com.cybersoft.crm04.entity.RolesEntity;
import com.cybersoft.crm04.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/role")
public class RoleController {

    /**
     * Chức năng add role:
     * B1: Xác định nghiệp vụ cho chức năng (thêm dữ liệu vào bảng roles)
     * B2: Xác định câu truy vấn cho chức năng add role (INSERT INTO roles ...)
     * B3: Từ câu truy vấn xác định được số lượng tham số sẽ sử dụng cho controller (đường dẫn đã khai báo)
     * -- Xử lý nghiệp vụ thông qua code --
     * B4: Để thực hiện được các câu truy vấn liên quan tới bảng đã được xác định ở B2 thì phải tạo ra file repository
     * để quản lý các câu truy vấn liên quan tới bảng nếu chưa có
     * B5: Xác định method tương ứng với câu truy vấn ở B2 của JPA (save)
     */

    @Autowired
    private RoleService roleService;

    /**
     * method save() trong JPA có 2 chức năng: vừa là thêm mới dữ liệu, vừa là cập nhật dữ liệu
     * - Thêm mới: khoá chính của class entity truyền vào hàm save() không có giá trị
     * - Cập nhật: khoá chính của class entity truyền vào hàm save() có giá trị
     */

    @GetMapping("/add")
    public String addRole() {
//        RolesEntity rolesEntity = new RolesEntity();
//        rolesEntity.setId(4);
//        rolesEntity.setName("ROLE_LEADER");
//        rolesEntity.setDescription("Leader dự án");
//        rolesRepository.save(rolesEntity); // method save() có setId -> Update

        return "role-add";
    }

    /**
     * BVN buổi 29:
     * - chỉnh link /role thành /role/add và fix lỗi liên quan đến css và js bên file HTML
     * - nếu thêm thành công thì xuất ra màn hình thông báo "Thêm thành công", ngược lại xuất ra thông báo "Thêm thất bại"
     */

    @PostMapping("/add")
    public String progressAddRole(Model model, @RequestParam String roleName, @RequestParam String roleDesc) {
        boolean isAddRoleSuccess = roleService.addRoleSuccess(roleName, roleDesc);
        model.addAttribute("isAddRoleSuccess", isAddRoleSuccess);

        return "role-add";
    }

    // Yêu cầu lấy toàn bộ danh sách role và hiển thị lên giao diện role-table.html
    // Thực hiện chức năng xoá role
    // Thực hiện chức năng thêm mới thành viên
    @GetMapping("/show")
    public String showListRoles(Model model) {

        List<RolesEntity> listRoles = roleService.getAllRoles();
        model.addAttribute("roles", listRoles);

        return "role-table";
    }

    @GetMapping("/delete/{roleId}")
    public String progressDeleteRoleById(@PathVariable(name = "roleId") int id) { // Phương thức xử lý xoá role theo id
        roleService.deleteRoleById(id);

        return "redirect:/role/show";
    }

//    @GetMapping("/update/{roleId}")
//    public String showUpdateRole(Model model, @PathVariable(name = "roleId") int id,
//                                 @RequestParam(defaultValue = "false", required = false) boolean update) {
//        RolesEntity updateRole = roleService.getRolesEntityById(id);
//        if (updateRole != null) {
//            model.addAttribute("roleEntity", updateRole);
//        }
//
//        return "role-update";
//    }

    @GetMapping("/update/{roleId}")
    public String showUpdateRole(Model model, @PathVariable int roleId) {
        Optional<RolesEntity> optionalRolesEntity = roleService.getOptionalRoleById(roleId);
        optionalRolesEntity.ifPresent(value -> {
            model.addAttribute("updateRoleEntity", value);
        });

        return "role-update";
    }

    @PostMapping("/update/{roleId}")
    public String progressUpdateRole(Model model, @PathVariable int roleId, @RequestParam String roleName, @RequestParam String roleDesc) {
        // Khởi tạo 1 đối tượng RolesEntity và gán giá trị cho 3 thuộc tính của đối tượng đó
        RolesEntity updateRoleEntity = new RolesEntity();
        updateRoleEntity.setId(roleId); // có setId -> update
        updateRoleEntity.setName(roleName); // gán roleName mới mà người dùng nhập vào form update
        updateRoleEntity.setDescription(roleDesc); // gán roleDesc mới mà người dùng nhập vào form update
        boolean isUpdateSuccess = roleService.updateRoleSuccess(updateRoleEntity);
        model.addAttribute("isUpdateSuccess", isUpdateSuccess); // thêm isUpdateSuccess vào model để hiển thị ra giao diện thành công hay thất bại
        model.addAttribute("updateRoleEntity", updateRoleEntity); // thêm updateRoleEntity vào model để tránh lỗi NullPointerException

        return "role-update";
    }
}
