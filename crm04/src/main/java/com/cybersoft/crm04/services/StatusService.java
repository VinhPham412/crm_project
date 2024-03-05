package com.cybersoft.crm04.services;

import com.cybersoft.crm04.entity.JobsEntity;
import com.cybersoft.crm04.entity.StatusEntity;
import com.cybersoft.crm04.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    public List<StatusEntity> getAllStatus() {
        return statusRepository.findAll();
    }

    public Optional<StatusEntity> getOptionalStatusById(int statusId) {
        return statusRepository.findById(statusId);
    }

    public StatusEntity getStatusEntityById(int statusId) {
        // Kiểu Optional: có hoặc không có cũng được
        // Optional chứa các method hỗ trợ sẵn giúp kiểm tra giá trị có null hay không để tránh bị lỗi null dữ liệu trong quá trình xử lý
        Optional<StatusEntity> optionalStatus = getOptionalStatusById(statusId);
        StatusEntity statusEntity = null;
        if (optionalStatus.isPresent()) { // isPresent() kiểm tra giá trị trong Optional có tồn tại hay không
            statusEntity = optionalStatus.get(); // get() giúp huỷ Optional, trả về kiểu dữ liệu thực của biến
        }

        return statusEntity;
    }

    public StatusEntity getStatusEntityByName(String name) {
        return statusRepository.findByName(name);
    }

}
