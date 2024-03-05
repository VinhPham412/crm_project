package com.cybersoft.crm04.repository;

import com.cybersoft.crm04.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<RolesEntity, Integer> {
    RolesEntity findByName(String name);
}
