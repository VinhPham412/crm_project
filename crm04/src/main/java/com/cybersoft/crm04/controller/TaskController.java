package com.cybersoft.crm04.controller;

import com.cybersoft.crm04.entity.JobsEntity;
import com.cybersoft.crm04.entity.StatusEntity;
import com.cybersoft.crm04.entity.TasksEntity;
import com.cybersoft.crm04.entity.UsersEntity;
import com.cybersoft.crm04.services.JobService;
import com.cybersoft.crm04.services.StatusService;
import com.cybersoft.crm04.services.TaskService;
import com.cybersoft.crm04.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private StatusService statusService;

    @GetMapping("/show")
    public String showAllTasks(Model model, HttpServletRequest request) {
        taskService.progressShowTasks(model, request);

        return "task";
    }

    @GetMapping("/add")
    public String addTask(Model model, HttpServletRequest request) {
        taskService.progressShowListJobAndUser(model, request);

        return "task-add";
    }

    @PostMapping("/add")
    public String progressAddTask(Model model, HttpServletRequest request, @RequestParam String taskName,
                                  @RequestParam("selectedJobId") int jobId, @RequestParam("selectedUserId") int userId,
                                  @RequestParam("startDate")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam("endDate")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UsersEntity userEntity = userService.getUsersEntityById(userId);
        JobsEntity jobEntity = jobService.getJobsEntityById(jobId);
        boolean isAddTaskSuccess = taskService.addTaskSuccess(taskName, userEntity, jobEntity, startDate, endDate);
        model.addAttribute("isAddTaskSuccess", isAddTaskSuccess);
        // điền sẵn các thông tin mà người dùng đã nhập trong thẻ form sau khi click button "Thêm dự án"
        model.addAttribute("taskName", taskName);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        // thêm 2 key "listJobs" và "listUsers" vào model để hiển thị danh sách dự án và danh sách nhân viên theo role ra bên giao diện html khi submit
        taskService.progressShowListJobAndUser(model, request);

        return "task-add";
    }

    @GetMapping("/delete/{taskId}")
    public String progressDeleteTaskById(@PathVariable(name = "taskId") int id) { // Phương thức xử lý xoá task theo id
        taskService.deleteTaskById(id);

        return "redirect:/task/show";
    }

    @GetMapping("/update/{taskId}")
    public String showUpdateTask(Model model, HttpServletRequest request, @PathVariable int taskId) {
        Optional<TasksEntity> optionalTasksEntity = taskService.getOptionalTaskById(taskId);
        optionalTasksEntity.ifPresent(value -> {
            model.addAttribute("updateTaskEntity", value);
        });
        taskService.progressShowListJobAndUser(model, request);
        List<StatusEntity> listStatus = statusService.getAllStatus();
        model.addAttribute("listStatus", listStatus);

        return "task-update";
    }

    @PostMapping("/update/{taskId}")
    public String progressUpdateTask(Model model, HttpServletRequest request, @PathVariable int taskId, @RequestParam String taskName,
                                     @RequestParam("selectedJobId") int jobId, @RequestParam("selectedUserId") int userId, @RequestParam("selectedStatusId") int statusId,
                                     @RequestParam("startDate")
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                     @RequestParam("endDate")
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // Khởi tạo 1 đối tượng TasksEntity và gán giá trị cho các thuộc tính của đối tượng đó (có setId -> update)
        TasksEntity updateTaskEntity = new TasksEntity();
        updateTaskEntity.setId(taskId); // có setId -> update
        updateTaskEntity.setName(taskName);
        updateTaskEntity.setStartDate(startDate);
        updateTaskEntity.setEndDate(endDate);
        UsersEntity userEntity = userService.getUsersEntityById(userId);
        updateTaskEntity.setTaskUsersEntity(userEntity);
        JobsEntity jobEntity = jobService.getJobsEntityById(jobId);
        updateTaskEntity.setJobsEntity(jobEntity);
        StatusEntity statusEntity = statusService.getStatusEntityById(statusId);
        updateTaskEntity.setStatusEntity(statusEntity);
        boolean isUpdateSuccess = taskService.updateTaskSuccess(updateTaskEntity);
        model.addAttribute("isUpdateSuccess", isUpdateSuccess); // thêm isUpdateSuccess vào model để hiển thị ra giao diện thành công hay thất bại
        model.addAttribute("updateTaskEntity", updateTaskEntity); // thêm updateTaskEntity vào model để tránh lỗi NullPointerException
        taskService.progressShowListJobAndUser(model, request);
        List<StatusEntity> listStatus = statusService.getAllStatus();
        model.addAttribute("listStatus", listStatus);

        return "task-update";
    }

    @GetMapping("/details/{taskId}")
    public String showTaskDetails(Model model, @PathVariable int taskId) {
        Optional<TasksEntity> optionalTasksEntity = taskService.getOptionalTaskById(taskId);
        optionalTasksEntity.ifPresent(value -> {
            model.addAttribute("taskEntity", value);
        });
        List<StatusEntity> listStatus = statusService.getAllStatus();
        model.addAttribute("listStatus", listStatus);

        return "task-details";
    }

}
