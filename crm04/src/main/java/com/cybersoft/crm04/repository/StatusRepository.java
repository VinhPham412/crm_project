package com.cybersoft.crm04.repository;

import com.cybersoft.crm04.entity.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, Integer> {
    StatusEntity findByName(String name);
}
