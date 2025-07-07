package com.brass_admin_d.brass_admin_d.group.dao;

import com.brass_admin_d.brass_admin_d.group.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // Поиск всех групп в рамках одной глобальной сессии
    List<Group> findBySessionId(Long sessionId);
}