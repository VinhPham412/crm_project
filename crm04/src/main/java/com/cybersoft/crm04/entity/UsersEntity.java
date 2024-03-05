package com.cybersoft.crm04.entity;

import jakarta.persistence.*;

import java.util.List;

/**
 * Cách xác định mối quan hệ giữa 2 bảng và mapping khoá ngoại trong Entity:
 * - B1: Xem khoá chính 2 bảng đang có quan hệ với nhau có phải là tự động tăng hay không?
 *  + nếu khoá chính tự động tăng -> không phải quan hệ OneToOne -> quan hệ OneToMany (1-n)
 *  + nếu khoá chính không tự động tăng và vừa là khoá chính vừa là khoá ngoại -> quan hệ OneToOne (1-1)
 * - B2(*): Mapping
 *  + Nếu là OneToMany hoặc ManyToOne:
 *  Entity nào giữ khoá ngoại thì sẽ có 2 Annotation sau: @ManyToOne và @JoinColumn
 *  Bảng được tham chiếu khoá ngoại sẽ map ngược lại và có annotation @OneToMany
 *  + Nếu là OneToOne: cả 2 bảng sẽ có Annotation @OneToOne, 1 trong 2 bảng sẽ có Annotation @JoinColumn
 */

@Entity(name = "users")
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "username")
    private String username;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "avatar")
    private String avatar;

//    @Column(name = "role_id")
//    private int roleId;

    @ManyToOne
    @JoinColumn(name = "role_id") // tên cột khoá ngoại trong database dùng để liên kết dữ liệu
    private RolesEntity rolesEntity; // dựa vào từ đằng sau (One or Many) của annotation OneToMany hay ManyToOne thì sẽ biết được là 1 đối tượng hay 1 list đối tượng

    @OneToMany(mappedBy = "jobUsersEntity")
    private List<JobsEntity> jobs;

    @OneToMany(mappedBy = "taskUsersEntity")
    private List<TasksEntity> tasks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        String[] words = this.fullName.trim().split("\\s+");
        return words[words.length - 1];
    }

    public String getLastName() {
        StringBuilder lastName = new StringBuilder();
        String[] words = this.fullName.trim().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            lastName.append(words[i]).append(" ");
        }
        return lastName.toString().trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public RolesEntity getRolesEntity() {
        return rolesEntity;
    }

    public void setRolesEntity(RolesEntity rolesEntity) {
        this.rolesEntity = rolesEntity;
    }

    public List<JobsEntity> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobsEntity> jobs) {
        this.jobs = jobs;
    }

    public List<TasksEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<TasksEntity> tasks) {
        this.tasks = tasks;
    }
}
