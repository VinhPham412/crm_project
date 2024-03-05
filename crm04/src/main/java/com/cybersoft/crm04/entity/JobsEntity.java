package com.cybersoft.crm04.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "jobs")
public class JobsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "user_id") // tên cột khoá ngoại trong database dùng để liên kết dữ liệu
    private UsersEntity jobUsersEntity;

    @OneToMany(mappedBy = "jobsEntity")
    List<TasksEntity> tasks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public UsersEntity getJobUsersEntity() {
        return jobUsersEntity;
    }

    public void setJobUsersEntity(UsersEntity jobUsersEntity) {
        this.jobUsersEntity = jobUsersEntity;
    }

    public List<TasksEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<TasksEntity> tasks) {
        this.tasks = tasks;
    }
}
