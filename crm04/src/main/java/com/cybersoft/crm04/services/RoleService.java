package com.cybersoft.crm04.services;

import com.cybersoft.crm04.entity.RolesEntity;
import com.cybersoft.crm04.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Lưu ý tên service sẽ giống tên controller, bởi vì Service là nơi xử lý logic code cho Controller
@Service // đưa service lên IOC
public class RoleService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<RolesEntity> getAllRoles() {
        return rolesRepository.findAll();
    }

    public Optional<RolesEntity> getOptionalRoleById(int roleId) {
        return rolesRepository.findById(roleId);
    }

    public RolesEntity getRolesEntityById(int roleId) {
        // Kiểu Optional: có hoặc không có cũng được
        // Optional chứa các method hỗ trợ sẵn giúp kiểm tra giá trị có null hay không để tránh bị lỗi null dữ liệu trong quá trình xử lý
        Optional<RolesEntity> optionalRole = getOptionalRoleById(roleId);
        RolesEntity rolesEntity = null;
        if (optionalRole.isPresent()) { // isPresent() kiểm tra giá trị trong Optional có tồn tại hay không
            rolesEntity = optionalRole.get(); // get() giúp huỷ Optional, trả về kiểu dữ liệu thực của biến
        }

        return rolesEntity;
    }

    public void deleteRoleById(int roleId) {
        rolesRepository.deleteById(roleId);
    }

    public boolean isRoleExist(String roleName) { // method kiểm tra xem roleName có tồn tại trong CSDL hay chưa
        List<RolesEntity> listRoles = rolesRepository.findAll(); // lấy toàn bộ danh sách roles từ database
        boolean isExist = false; // biến flag kiểm tra xem roleName có tồn tại trong database hay không, mặc định là false
        for (RolesEntity role : listRoles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                isExist = true; // roleName có tồn tại trong database
                break;
            }
        }
        return isExist;
    }

    public boolean addRoleSuccess(String roleName, String roleDesc) { // method kiểm tra xem có add role thành công hay không
        RolesEntity addRoleEntity = new RolesEntity();
        addRoleEntity.setName(roleName);
        addRoleEntity.setDescription(roleDesc);

        boolean isAddSuccess = false; // biến flag kiểm tra xem có thêm role thành công hay không, mặc định là không (false)
        try {
            if (!isRoleExist(roleName)) { // yêu cầu khi thêm thì roleName phải khác nhau, nếu roleName không tồn tại thì mới add role
                rolesRepository.save(addRoleEntity);
                isAddSuccess = true; // thêm thành công và không xảy ra lỗi Runtime
            } else {
                System.out.println(roleName + " đã tồn tại!");
            }
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi thêm dữ liệu: " + e.getMessage());
        }
        return isAddSuccess;
    }

    public boolean updateRoleSuccess(RolesEntity rolesEntity) { // method kiểm tra xem có update role thành công hay không
        boolean isUpdateSuccess = false; // biến flag kiểm tra xem có update role thành công hay không, mặc định là không (false)
        try {
            rolesRepository.save(rolesEntity); // nếu rolesEntity có setId -> update
            isUpdateSuccess = true; // update thành công và không xảy ra lỗi Runtime
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi update dữ liệu: " + e.getMessage());
        }
        return isUpdateSuccess;
    }

}
