package com.brass_admin_d.brass_admin_d.session.service;

import com.brass_admin_d.brass_admin_d.department.dto.DepartmentDto;
import com.brass_admin_d.brass_admin_d.department.model.Department;
import com.brass_admin_d.brass_admin_d.department.dao.DepartmentRepository;
import com.brass_admin_d.brass_admin_d.session.dao.SessionsGlobalRepository;
import com.brass_admin_d.brass_admin_d.session.dto.GlobalSessionsDto;
import com.brass_admin_d.brass_admin_d.session.dto.NewGlobalSessionsDto;
import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionsGlobalServiceImpl implements SessionsGlobalService {

    private final SessionsGlobalRepository sessionsGlobalRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public GlobalSessionsDto createSession(NewGlobalSessionsDto requestDto) {
        // Находим департамент, к которому будет привязана сессия
        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + requestDto.getDepartmentId()));

        SessionsGlobal newSession = new SessionsGlobal();
        newSession.setCity(requestDto.getCity());
        newSession.setDepartment(department);
        newSession.setSessionDate(LocalDate.now());

        SessionsGlobal savedSession = sessionsGlobalRepository.save(newSession);
        return sessionsGlobalToDto(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalSessionsDto getSessionById(Long id) {
        SessionsGlobal session = sessionsGlobalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Global Session not found with id: " + id));
        return modelMapper.map(session, GlobalSessionsDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GlobalSessionsDto> getAllSessions() {
        return sessionsGlobalRepository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, GlobalSessionsDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GlobalSessionsDto updateSession(Long id, GlobalSessionsDto requestDto) {
        SessionsGlobal sessionToUpdate = sessionsGlobalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Global Session not found with id: " + id));
        sessionToUpdate.setCity(requestDto.getCity());
        // Дату сессии можно обновлять, а можно и не трогать. Оставим без изменений.

        SessionsGlobal updatedSession = sessionsGlobalRepository.save(sessionToUpdate);
        return modelMapper.map(updatedSession, GlobalSessionsDto.class);
    }

    @Override
    @Transactional
    public Boolean deleteSession(Long id) {
        if (!sessionsGlobalRepository.existsById(id)) {
            throw new EntityNotFoundException("Global Session not found with id: " + id);
        }
        sessionsGlobalRepository.deleteById(id);
        return true;
    }

    // =================================================================
    // ПРИВАТНЫЕ МЕТОДЫ-ХЕЛПЕРЫ ДЛЯ ПРЕОБРАЗОВАНИЯ В DTO
    // =================================================================

    /**
     * Преобразует сущность SessionsGlobal в SessionsGlobalResponseDto.
     * Этот приватный метод централизует логику маппинга внутри сервиса,
     * разрывая циклическую зависимость при сериализации.
     *
     * @param session Сущность SessionsGlobal для преобразования.
     * @return Объект SessionsGlobalResponseDto.
     */
    private GlobalSessionsDto sessionsGlobalToDto(SessionsGlobal session) {
        if (session == null) {
            return null;
        }

        GlobalSessionsDto dto = new GlobalSessionsDto();
        dto.setId(session.getId());
        dto.setSessionDate(session.getSessionDate());
        dto.setCity(session.getCity());

        // Используем другой приватный хелпер для преобразования вложенной сущности
        dto.setDepartment(departmentToDto(session.getDepartment()));

        return dto;
    }

    /**
     * Вспомогательный метод для преобразования сущности Department в DepartmentResponseDto.
     *
     * @param department Сущность Department для преобразования.
     * @return Объект DepartmentResponseDto.
     */
    private DepartmentDto departmentToDto(Department department) {
        if (department == null) {
            return null;
        }

        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setDepartName(department.getDepartName());
        dto.setCreationDate(department.getCreationDate());

        // Важно: мы НЕ добавляем сюда список сессий, чтобы не создать цикл

        return dto;
    }
}