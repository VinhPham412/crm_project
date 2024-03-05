package com.cybersoft.crm04.services;

import com.cybersoft.crm04.entity.*;
import com.cybersoft.crm04.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private StatusRepository statusRepository;

    public List<TasksEntity> getAllTasks() {
        return tasksRepository.findAll();
    }

    public void deleteTaskById(int taskId) {
        tasksRepository.deleteById(taskId);
    }

    public Optional<TasksEntity> getOptionalTaskById(int taskId) {
        return tasksRepository.findById(taskId);
    }

    public TasksEntity getTasksEntityById(int taskId) {
        // Kiểu Optional: có hoặc không có cũng được
        // Optional chứa các method hỗ trợ sẵn giúp kiểm tra giá trị có null hay không để tránh bị lỗi null dữ liệu trong quá trình xử lý
        Optional<TasksEntity> optionalTask = getOptionalTaskById(taskId);
        TasksEntity tasksEntity = null;
        if (optionalTask.isPresent()) { // isPresent() kiểm tra giá trị trong Optional có tồn tại hay không
            tasksEntity = optionalTask.get(); // get() giúp huỷ Optional, trả về kiểu dữ liệu thực của biến
        }

        return tasksEntity;
    }

    public List<TasksEntity> getListTasksByJobEntity(JobsEntity jobsEntity) {
        return tasksRepository.findByJobsEntity(jobsEntity);
    }

    public List<TasksEntity> getListTasksByUserEntity(UsersEntity usersEntity) {
        return tasksRepository.findByTaskUsersEntity(usersEntity);
    }

    public List<TasksEntity> getListTasksByStatusName(String nameOfStatus) {
        StatusEntity statusEntity = statusRepository.findByName(nameOfStatus);
        return tasksRepository.findByStatusEntity(statusEntity);
    }

    public List<TasksEntity> getListTasksByUsersEntityAndStatusEntity(UsersEntity usersEntity, StatusEntity statusEntity) {
        return tasksRepository.findByTaskUsersEntityAndStatusEntity(usersEntity, statusEntity);
    }

    public List<TasksEntity> getTasksByJobAndUserAndStatusName(JobsEntity jobsEntity, UsersEntity usersEntity, String statusName) {
        StatusEntity statusEntity = statusRepository.findByName(statusName);
        return tasksRepository.findByJobsEntityAndTaskUsersEntityAndStatusEntity(jobsEntity, usersEntity, statusEntity);
    }

    public List<UsersEntity> getListUsersEntityByJobsEntity(JobsEntity jobsEntity) {
        return tasksRepository.findDistinctUsersEntityByJobsEntity(jobsEntity);
    }

    public List<TasksEntity> getListOfTasksEntityByJobsEntityAndUsersEntity(JobsEntity jobsEntity, UsersEntity usersEntity) {
        return tasksRepository.findByJobsEntityAndTaskUsersEntity(jobsEntity, usersEntity);
    }

    public void progressShowTasks(Model model, HttpServletRequest request) {
        String roleName = request.getSession().getAttribute("role").toString();
        String email = request.getSession().getAttribute("email").toString();
        List<TasksEntity> listTasks = new ArrayList<>();
        String title = "";
        if (roleName.equalsIgnoreCase("ROLE_ADMIN")) {
            // nếu user có ROLE_ADMIN thì sẽ hiển thị danh sách tất cả công việc
            listTasks = getAllTasks();
            title = "Danh sách tất cả công việc trong hệ thống mà ADMIN có thể quản lý";
        } else if (roleName.equalsIgnoreCase("ROLE_LEADER")) {
            // nếu user có ROLE_LEADER thì sẽ hiển thị danh sách tất cả công việc của các dự án mà leader đó quản lý
            UsersEntity leaderEntity = usersRepository.findByEmail(email); // lấy đối tượng leader
            List<JobsEntity> listJobs = leaderEntity.getJobs(); // lấy danh sách các job entity mà leader đó quản lý
            for (JobsEntity jobsEntity : listJobs) {
                List<TasksEntity> tasks = getListTasksByJobEntity(jobsEntity);
                listTasks.addAll(tasks);
            }
            title = "Danh sách tất cả công việc thuộc các dự án mà leader quản lý";
        } else {
            // nếu user có ROLE_USER (nhân viên) thì sẽ hiển thị danh sách các công việc mà nhân viên đó cần thực hiện
            UsersEntity userEntity = usersRepository.findByEmail(email); // lấy đối tượng user
            listTasks = getListTasksByUserEntity(userEntity);
            title = "Danh sách tất cả công việc mà nhân viên cần thực hiện";
        }
        if (listTasks != null) {
            model.addAttribute("listTasks", listTasks);
        }
        model.addAttribute("title", title);
    }

    public void progressShowListJobAndUser(Model model, HttpServletRequest request) {
        String roleName = request.getSession().getAttribute("role").toString();
        String email = request.getSession().getAttribute("email").toString();
        RolesEntity roleUser = rolesRepository.findByName("ROLE_USER");
        List<UsersEntity> listUsers = usersRepository.findByRolesEntity(roleUser); // lấy danh sách user là nhân viên thường
        List<JobsEntity> listJobs = new ArrayList<>();
        if (roleName.equalsIgnoreCase("ROLE_ADMIN")) {
            // nếu user có ROLE_ADMIN thì sẽ hiển thị danh sách tất cả dự án của hệ thống
            listJobs = jobsRepository.findAll();
        }
        if (roleName.equalsIgnoreCase("ROLE_LEADER")) {
            // nếu user có ROLE_LEADER thì sẽ hiển thị danh sách tất cả các dự án mà leader đó quản lý
            UsersEntity leaderEntity = usersRepository.findByEmail(email); // lấy đối tượng leader
            listJobs = leaderEntity.getJobs(); // lấy danh sách các job entity mà leader đó quản lý
        }
        if (listJobs != null) {
            model.addAttribute("listJobs", listJobs);
        }
        if (listUsers != null) {
            model.addAttribute("listUsers", listUsers);
        }
    }

    public boolean isTaskExist(TasksEntity tasksEntity) { // method kiểm tra xem task có tồn tại trong CSDL hay chưa
        List<TasksEntity> listTasks = getAllTasks(); // lấy toàn bộ danh sách tasks từ database
        boolean isExist = false; // biến flag kiểm tra xem task có tồn tại trong database hay không, mặc định là không (false)
        for (TasksEntity task : listTasks) {
            if (task.getName().equalsIgnoreCase(tasksEntity.getName()) && (task.getJobsEntity().getId() == tasksEntity.getJobsEntity().getId())) {
                // kiểm tra nếu task name và job_id khớp nhau -> task đó đã được phân công hay đã tồn tại trong database
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    // method kiểm tra xem có add task thành công hay không
    public boolean addTaskSuccess(String taskName, UsersEntity usersEntity, JobsEntity jobsEntity, LocalDate startDate, LocalDate endDate) {
        TasksEntity addTaskEntity = new TasksEntity();
        addTaskEntity.setName(taskName);
        addTaskEntity.setStartDate(startDate);
        addTaskEntity.setEndDate(endDate);
        addTaskEntity.setTaskUsersEntity(usersEntity);
        addTaskEntity.setJobsEntity(jobsEntity);
        StatusEntity statusEntity = statusRepository.findByName("Chưa thực hiện"); // khi thêm mới task thì trạng thái mặc định là "Chưa thực hiện"
        addTaskEntity.setStatusEntity(statusEntity);

        boolean isAddSuccess = false; // biến flag kiểm tra xem có thêm task thành công hay không, mặc định là không (false)
        try {
            if (!isTaskExist(addTaskEntity)) { // nếu task không tồn tại thì mới add task
                tasksRepository.save(addTaskEntity); // không có setId -> add
                isAddSuccess = true; // thêm thành công và không xảy ra lỗi Runtime
            } else {
                System.out.println("Task đã tồn tại!");
            }
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi thêm dữ liệu: " + e.getMessage());
        }
        return isAddSuccess;
    }

    public boolean updateTaskSuccess(TasksEntity tasksEntity) { // method kiểm tra xem có update task thành công hay không
        boolean isUpdateSuccess = false; // biến flag kiểm tra xem có update task thành công hay không, mặc định là không (false)
        try {
            tasksRepository.save(tasksEntity); // nếu tasksEntity có setId -> update
            isUpdateSuccess = true; // update thành công và không xảy ra lỗi Runtime
        } catch (Exception e) {
            // đoạn code bên trong catch chỉ được chạy khi đoạn code bên trong try bị lỗi (Runtime Error)
            System.out.println("Lỗi update dữ liệu: " + e.getMessage());
        }
        return isUpdateSuccess;
    }

    public int getAmountOfUnimplementedTask() { // lấy số lượng công việc chưa thực hiện
        return getListTasksByStatusName("Chưa thực hiện").size();
    }

    public int getAmountOfInProgressTask() { // lấy số lượng công việc đang thực hiện
        return getListTasksByStatusName("Đang thực hiện").size();
    }

    public int getAmountOfCompletedTask() { // lấy số lượng công việc đã hoàn thành
        return getAllTasks().size() - getAmountOfUnimplementedTask() - getAmountOfInProgressTask();
    }

    public int getThePercentageOfUnimplementedTaskByUser(UsersEntity usersEntity) { // lấy phần trăm (%) công việc chưa thực hiện của 1 người dùng
        int totalAmountOfTask = 0;
        int amountOfUnimplementedTasks = 0;
        StatusEntity statusEntity = statusRepository.findByName("Chưa thực hiện");
        String roleName = usersEntity.getRolesEntity().getName();
        if (roleName.equalsIgnoreCase("ROLE_USER")) {
            totalAmountOfTask = getListTasksByUserEntity(usersEntity).size(); // tổng số công việc của 1 user
            amountOfUnimplementedTasks = getListTasksByUsersEntityAndStatusEntity(usersEntity, statusEntity).size(); // tổng số công việc chưa thực hiện của 1 user
        } else if (roleName.equalsIgnoreCase("ROLE_ADMIN")) {
            totalAmountOfTask = getAllTasks().size();
            amountOfUnimplementedTasks = getListTasksByStatusName("Chưa thực hiện").size();
        } else { // ROLE_LEADER
            List<JobsEntity> listJobs = usersEntity.getJobs(); // danh sách các dự án mà leader quản lý
            for (JobsEntity job : listJobs) {
                totalAmountOfTask += getListTasksByJobEntity(job).size();
                amountOfUnimplementedTasks += tasksRepository.findByJobsEntityAndStatusEntity(job, statusEntity).size();
            }
        }
        return Math.round(1.0f * amountOfUnimplementedTasks / totalAmountOfTask * 100);
    }

    public int getThePercentageOfInProgressTaskByUser(UsersEntity usersEntity) { // lấy phần trăm (%) công việc đang thực hiện của 1 người dùng
        int totalAmountOfTask = 0;
        int amountOfInProgressTasks = 0;
        StatusEntity statusEntity = statusRepository.findByName("Đang thực hiện");
        String roleName = usersEntity.getRolesEntity().getName();
        if (roleName.equalsIgnoreCase("ROLE_USER")) {
            totalAmountOfTask = getListTasksByUserEntity(usersEntity).size(); // tổng số công việc của 1 user
            amountOfInProgressTasks = getListTasksByUsersEntityAndStatusEntity(usersEntity, statusEntity).size(); // tổng số công việc đang thực hiện của 1 user
        } else if (roleName.equalsIgnoreCase("ROLE_ADMIN")) {
            totalAmountOfTask = getAllTasks().size();
            amountOfInProgressTasks = getListTasksByStatusName("Đang thực hiện").size();
        } else { // ROLE_LEADER
            List<JobsEntity> listJobs = usersEntity.getJobs(); // danh sách các dự án mà leader quản lý
            for (JobsEntity job : listJobs) {
                totalAmountOfTask += getListTasksByJobEntity(job).size();
                amountOfInProgressTasks += tasksRepository.findByJobsEntityAndStatusEntity(job, statusEntity).size();
            }
        }
        return Math.round(1.0f * amountOfInProgressTasks / totalAmountOfTask * 100);
    }

    public int getThePercentageOfCompletedTaskByUser(UsersEntity usersEntity) { // lấy phần trăm (%) công việc đã hoàn thành của 1 người dùng
        return 100 - getThePercentageOfUnimplementedTaskByUser(usersEntity) - getThePercentageOfInProgressTaskByUser(usersEntity);
    }

    public int getThePercentageOfUnimplementedTaskByJob(JobsEntity jobsEntity) { // lấy phần trăm (%) công việc chưa thực hiện của 1 dự án
        StatusEntity statusEntity = statusRepository.findByName("Chưa thực hiện");
        int totalAmountOfTask = getListTasksByJobEntity(jobsEntity).size();
        int amountOfUnimplementedTasks = tasksRepository.findByJobsEntityAndStatusEntity(jobsEntity, statusEntity).size();
        return Math.round(1.0f * amountOfUnimplementedTasks / totalAmountOfTask * 100);
    }

    public int getThePercentageOfInProgressTaskByJob(JobsEntity jobsEntity) { // lấy phần trăm (%) công việc đang thực hiện của 1 dự án
        StatusEntity statusEntity = statusRepository.findByName("Đang thực hiện");
        int totalAmountOfTask = getListTasksByJobEntity(jobsEntity).size();
        int amountOfInProgressTasks = tasksRepository.findByJobsEntityAndStatusEntity(jobsEntity, statusEntity).size();
        return Math.round(1.0f * amountOfInProgressTasks / totalAmountOfTask * 100);
    }

    public int getThePercentageOfCompletedTaskByJob(JobsEntity jobsEntity) { // lấy phần trăm (%) công việc đã hoàn thành của 1 dự án
        return 100 - getThePercentageOfUnimplementedTaskByJob(jobsEntity) - getThePercentageOfInProgressTaskByJob(jobsEntity);
    }

    // lấy danh sách các tasksEntity có trạng thái "chưa thực hiện" dựa trên đối tượng user đã login
    public List<TasksEntity> getListOfUnimplementedTasks(UsersEntity loginUsersEntity) {
        List<TasksEntity> listOfUnimplementedTasks = new ArrayList<>();
        StatusEntity unimplementedStatusEntity = statusRepository.findByName("Chưa thực hiện");
        String roleName = loginUsersEntity.getRolesEntity().getName();
        if (roleName.equalsIgnoreCase("ROLE_ADMIN")) {
            listOfUnimplementedTasks = getListTasksByStatusName("Chưa thực hiện");
        } else if (roleName.equalsIgnoreCase("ROLE_USER")) {
            listOfUnimplementedTasks = getListTasksByUsersEntityAndStatusEntity(loginUsersEntity, unimplementedStatusEntity);
        } else { // ROLE_LEADER
            List<JobsEntity> listJobs = loginUsersEntity.getJobs(); // danh sách các dự án mà leader quản lý
            for (JobsEntity job : listJobs) {
                List<TasksEntity> tasks = tasksRepository.findByJobsEntityAndStatusEntity(job, unimplementedStatusEntity);
                listOfUnimplementedTasks.addAll(tasks);
            }
        }
        return listOfUnimplementedTasks;
    }

    // lấy danh sách các tasksEntity có trạng thái "đang thực hiện" dựa trên đối tượng user đã login
    public List<TasksEntity> getListOfInProgressTasks(UsersEntity loginUsersEntity) {
        List<TasksEntity> listOfInProgressTasks = new ArrayList<>();
        StatusEntity inProgressStatusEntity = statusRepository.findByName("Đang thực hiện");
        String roleName = loginUsersEntity.getRolesEntity().getName();
        if (roleName.equalsIgnoreCase("ROLE_ADMIN")) {
            listOfInProgressTasks = getListTasksByStatusName("Đang thực hiện");
        } else if (roleName.equalsIgnoreCase("ROLE_USER")) {
            listOfInProgressTasks = getListTasksByUsersEntityAndStatusEntity(loginUsersEntity, inProgressStatusEntity);
        } else { // ROLE_LEADER
            List<JobsEntity> listJobs = loginUsersEntity.getJobs(); // danh sách các dự án mà leader quản lý
            for (JobsEntity job : listJobs) {
                List<TasksEntity> tasks = tasksRepository.findByJobsEntityAndStatusEntity(job, inProgressStatusEntity);
                listOfInProgressTasks.addAll(tasks);
            }
        }
        return listOfInProgressTasks;
    }

    // lấy danh sách các tasksEntity có trạng thái "đã hoàn thành" dựa trên đối tượng user đã login
    public List<TasksEntity> getListOfCompletedTasks(UsersEntity loginUsersEntity) {
        List<TasksEntity> listOfCompletedTasks = new ArrayList<>();
        StatusEntity completedStatusEntity = statusRepository.findByName("Đã hoàn thành");
        String roleName = loginUsersEntity.getRolesEntity().getName();
        if (roleName.equalsIgnoreCase("ROLE_ADMIN")) {
            listOfCompletedTasks = getListTasksByStatusName("Đã hoàn thành");
        } else if (roleName.equalsIgnoreCase("ROLE_USER")) {
            listOfCompletedTasks = getListTasksByUsersEntityAndStatusEntity(loginUsersEntity, completedStatusEntity);
        } else { // ROLE_LEADER
            List<JobsEntity> listJobs = loginUsersEntity.getJobs(); // danh sách các dự án mà leader quản lý
            for (JobsEntity job : listJobs) {
                List<TasksEntity> tasks = tasksRepository.findByJobsEntityAndStatusEntity(job, completedStatusEntity);
                listOfCompletedTasks.addAll(tasks);
            }
        }
        return listOfCompletedTasks;
    }
}
