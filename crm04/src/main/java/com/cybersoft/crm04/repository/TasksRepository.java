package com.cybersoft.crm04.repository;

import com.cybersoft.crm04.entity.JobsEntity;
import com.cybersoft.crm04.entity.StatusEntity;
import com.cybersoft.crm04.entity.TasksEntity;
import com.cybersoft.crm04.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<TasksEntity, Integer> {
    List<TasksEntity> findByJobsEntity(JobsEntity jobsEntity);

    List<TasksEntity> findByTaskUsersEntity(UsersEntity usersEntity);

    List<TasksEntity> findByStatusEntity(StatusEntity statusEntity);

    List<TasksEntity> findByTaskUsersEntityAndStatusEntity(UsersEntity usersEntity, StatusEntity statusEntity);

    List<TasksEntity> findByJobsEntityAndTaskUsersEntityAndStatusEntity(JobsEntity jobsEntity, UsersEntity usersEntity, StatusEntity statusEntity);

    List<TasksEntity> findByJobsEntityAndStatusEntity(JobsEntity jobsEntity, StatusEntity statusEntity);

    // Định nghĩa một truy vấn để lấy tất cả UsersEntity thông qua JobsEntity
    @Query("SELECT DISTINCT t.taskUsersEntity FROM tasks t WHERE t.jobsEntity = :jobEntity")
    List<UsersEntity> findDistinctUsersEntityByJobsEntity(@Param("jobEntity") JobsEntity jobEntity);

    List<TasksEntity> findByJobsEntityAndTaskUsersEntity(JobsEntity jobsEntity, UsersEntity usersEntity);

}
