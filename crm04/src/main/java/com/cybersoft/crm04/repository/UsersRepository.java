package com.cybersoft.crm04.repository;

import com.cybersoft.crm04.entity.RolesEntity;
import com.cybersoft.crm04.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Integer> {
    List<UsersEntity> findByEmailAndPassword(String email, String password);

    UsersEntity findByEmail(String email);

    List<UsersEntity> findByRolesEntity(RolesEntity rolesEntity);
}
