package com.cybersoft.crm04.services;

import com.cybersoft.crm04.entity.JobsEntity;
import com.cybersoft.crm04.entity.UsersEntity;
import com.cybersoft.crm04.repository.JobsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobsRepository jobsRepository;

    public List<JobsEntity> getAllJobs() {
        return jobsRepository.findAll();
    }

    public void deleteJobById(int jobId) {
        jobsRepository.deleteById(jobId);
    }

    public Optional<JobsEntity> getOptionalJobById(int jobId) {
        return jobsRepository.findById(jobId);
    }

    public JobsEntity getJobsEntityById(int jobId) {
        // Kiểu Optional: có hoặc không có cũng được
        // Optional chứa các method hỗ trợ sẵn giúp kiểm tra giá trị có null hay không để tránh bị lỗi null dữ liệu trong quá trình xử lý
        Optional<JobsEntity> optionalJob = getOptionalJobById(jobId);
        JobsEntity jobsEntity = null;
        if (optionalJob.isPresent()) { // isPresent() kiểm tra giá trị trong Optional có tồn tại hay không
            jobsEntity = optionalJob.get(); // get() giúp huỷ Optional, trả về kiểu dữ liệu thực của biến
        }

        return jobsEntity;
    }

    public boolean isJobNameExist(String jobName) { // method kiểm tra xem job name có tồn tại trong CSDL hay chưa
        List<JobsEntity> listJobs = getAllJobs(); // lấy toàn bộ danh sách jobs từ database
        boolean isExist = false; // biến flag kiểm tra xem job name có tồn tại trong database hay không, mặc định là không (false)
        for (JobsEntity job : listJobs) {
            if (job.getName().equalsIgnoreCase(jobName)) {
                isExist = true; // job name đã tồn tại trong database
                break;
            }
        }
        return isExist;
    }

    // method kiểm tra xem có add job thành công hay không
    public boolean addJobSuccess(String jobName, String jobDesc, LocalDate startDate, LocalDate endDate, UsersEntity leaderEntity) {
        JobsEntity addJobEntity = new JobsEntity();
        addJobEntity.setName(jobName);
        addJobEntity.setDescription(jobDesc);
        addJobEntity.setStartDate(startDate);
        addJobEntity.setEndDate(endDate);
        addJobEntity.setJobUsersEntity(leaderEntity);

        boolean isAddSuccess = false; // biến flag kiểm tra xem có thêm job thành công hay không, mặc định là không (false)
        try {
            if (!isJobNameExist(jobName)) { // yêu cầu khi thêm thì job name phải khác nhau, nếu job name không tồn tại thì mới add job
                jobsRepository.save(addJobEntity); // không có setId -> add
                isAddSuccess = true; // thêm thành công và không xảy ra lỗi Runtime
            } else {
                System.out.println(jobName + " đã tồn tại!");
            }
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi thêm dữ liệu: " + e.getMessage());
        }
        return isAddSuccess;
    }

    public boolean updateJobSuccess(JobsEntity jobsEntity) { // method kiểm tra xem có update job thành công hay không
        boolean isUpdateSuccess = false; // biến flag kiểm tra xem có update job thành công hay không, mặc định là không (false)
        try {
            jobsRepository.save(jobsEntity); // nếu jobsEntity có setId -> update
            isUpdateSuccess = true; // update thành công và không xảy ra lỗi Runtime
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi update dữ liệu: " + e.getMessage());
        }
        return isUpdateSuccess;
    }

}
