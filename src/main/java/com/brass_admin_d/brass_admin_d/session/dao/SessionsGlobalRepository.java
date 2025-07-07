package com.brass_admin_d.brass_admin_d.session.dao;

import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionsGlobalRepository extends JpaRepository<SessionsGlobal, Long> {
    // Поиск всех сессий для определенного департамента
    List<SessionsGlobal> findByDepartmentId(Long departmentId);
}