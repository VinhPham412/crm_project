package com.cybersoft.crm04.controller;

import com.cybersoft.crm04.entity.JobsEntity;
import com.cybersoft.crm04.entity.TasksEntity;
import com.cybersoft.crm04.entity.UsersEntity;
import com.cybersoft.crm04.services.JobService;
import com.cybersoft.crm04.services.TaskService;
import com.cybersoft.crm04.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/job")
public class JobController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/show")
    public String showAllJobs(Model model) {
        List<JobsEntity> listJobs = jobService.getAllJobs();
        model.addAttribute("listJobs", listJobs);

        return "groupwork";
    }

    @GetMapping("/details/{jobId}")
    public String showJobDetails(Model model, @PathVariable int jobId) { // show chi tiết dự án
        JobsEntity jobsEntity = jobService.getJobsEntityById(jobId);
        int percentageOfUnimplementedTask = taskService.getThePercentageOfUnimplementedTaskByJob(jobsEntity);
        int percentageOfInProgressTask = taskService.getThePercentageOfInProgressTaskByJob(jobsEntity);
        int percentageOfCompletedTask = taskService.getThePercentageOfCompletedTaskByJob(jobsEntity);
        model.addAttribute("percentageOfUnimplementedTask", percentageOfUnimplementedTask);
        model.addAttribute("percentageOfInProgressTask", percentageOfInProgressTask);
        model.addAttribute("percentageOfCompletedTask", percentageOfCompletedTask);
        List<UsersEntity> listUsers = taskService.getListUsersEntityByJobsEntity(jobsEntity);
        model.addAttribute("listUsers", listUsers);

        Map<UsersEntity, Map<String, List<TasksEntity>>> userTasksMap = new HashMap<>();

        for (UsersEntity user : listUsers) {
            Map<String, List<TasksEntity>> tasksByStatus = new HashMap<>();
            tasksByStatus.put("Chưa thực hiện", taskService.getTasksByJobAndUserAndStatusName(jobsEntity, user, "Chưa thực hiện"));
            tasksByStatus.put("Đang thực hiện", taskService.getTasksByJobAndUserAndStatusName(jobsEntity, user, "Đang thực hiện"));
            tasksByStatus.put("Đã hoàn thành", taskService.getTasksByJobAndUserAndStatusName(jobsEntity, user, "Đã hoàn thành"));

            userTasksMap.put(user, tasksByStatus);
        }

        model.addAttribute("userTasksMap", userTasksMap);

        return "groupwork-details";
    }

    @GetMapping("/add")
    public String addJob(Model model) {
        List<UsersEntity> listLeaders = userService.getAllLeaders();
        model.addAttribute("listLeaders", listLeaders);

        return "groupwork-add";
    }

    @PostMapping("/add")
    public String progressAddJob(Model model, @RequestParam String jobName, @RequestParam String jobDesc,
                                 @RequestParam("startDate")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("endDate")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 @RequestParam("selectedLeaderId") int leaderId) {
        UsersEntity leaderEntity = userService.getUsersEntityById(leaderId);
        boolean isAddJobSuccess = jobService.addJobSuccess(jobName, jobDesc, startDate, endDate, leaderEntity);
        model.addAttribute("isAddJobSuccess", isAddJobSuccess);
        // điền sẵn các thông tin mà người dùng đã nhập trong thẻ form sau khi click button "Thêm dự án"
        model.addAttribute("jobName", jobName);
        model.addAttribute("jobDesc", jobDesc);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        List<UsersEntity> listLeaders = userService.getAllLeaders();
        model.addAttribute("listLeaders", listLeaders);

        return "groupwork-add";
    }

    @GetMapping("/delete/{jobId}")
    public String progressDeleteJobById(@PathVariable(name = "jobId") int id) { // Phương thức xử lý xoá job theo id
        jobService.deleteJobById(id);

        return "redirect:/job/show";
    }

    @GetMapping("/update/{jobId}")
    public String showUpdateJob(Model model, @PathVariable int jobId) {
        Optional<JobsEntity> optionalJobsEntity = jobService.getOptionalJobById(jobId);
        optionalJobsEntity.ifPresent(value -> {
            model.addAttribute("updateJobEntity", value);
        });
        List<UsersEntity> listLeaders = userService.getAllLeaders();
        model.addAttribute("listLeaders", listLeaders);

        return "groupwork-update";
    }

    @PostMapping("/update/{jobId}")
    public String progressUpdateJob(Model model, @PathVariable int jobId, @RequestParam String jobName, @RequestParam String jobDesc,
                                    @RequestParam("startDate")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                    @RequestParam("endDate")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                    @RequestParam("selectedLeaderId") int leaderId) {
        // Khởi tạo 1 đối tượng JobsEntity và gán giá trị cho các thuộc tính của đối tượng đó (có setId -> update)
        JobsEntity updateJobEntity = new JobsEntity();
        updateJobEntity.setId(jobId); // có setId -> update
        updateJobEntity.setName(jobName);
        updateJobEntity.setDescription(jobDesc);
        updateJobEntity.setStartDate(startDate);
        updateJobEntity.setEndDate(endDate);
        UsersEntity leaderEntity = userService.getUsersEntityById(leaderId);
        updateJobEntity.setJobUsersEntity(leaderEntity);
        boolean isUpdateSuccess = jobService.updateJobSuccess(updateJobEntity);
        model.addAttribute("isUpdateSuccess", isUpdateSuccess); // thêm isUpdateSuccess vào model để hiển thị ra giao diện thành công hay thất bại
        model.addAttribute("updateJobEntity", updateJobEntity); // thêm updateJobEntity vào model để tránh lỗi NullPointerException
        List<UsersEntity> listLeaders = userService.getAllLeaders();
        model.addAttribute("listLeaders", listLeaders);

        return "groupwork-update";
    }

}
