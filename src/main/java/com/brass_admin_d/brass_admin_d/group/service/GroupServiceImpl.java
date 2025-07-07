package com.brass_admin_d.brass_admin_d.group.service;

import com.brass_admin_d.brass_admin_d.group.dto.GroupDto;
import com.brass_admin_d.brass_admin_d.group.dto.NewGroupDto;
import com.brass_admin_d.brass_admin_d.group.model.Group;
import com.brass_admin_d.brass_admin_d.group.dao.GroupRepository;
import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import com.brass_admin_d.brass_admin_d.session.dao.SessionsGlobalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final SessionsGlobalRepository sessionsGlobalRepository;

    @Override
    @Transactional
    public GroupDto createGroup(NewGroupDto groupDto) {
        SessionsGlobal session = sessionsGlobalRepository.findById(groupDto.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found with id: " + groupDto.getSessionId()));

        Group newGroup = new Group();
        newGroup.setGroupNumber(groupDto.getGroupNumber());
        newGroup.setSession(session);
        newGroup.setCreationDate(LocalDate.now()); // Устанавливаем дату создания

        Group savedGroup = groupRepository.save(newGroup);
        return groupToDto(savedGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDto getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));
        return groupToDto(group);
    }

    @Override
    @Transactional
    public Boolean deleteGroupById(Long id) {
        if (!groupRepository.existsById(id)) {
            // Можно бросить исключение или просто вернуть false
            return false;
        }
        groupRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll()
                .stream()
                .map(this::groupToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> getGroupBySessionId(Long sessionId) {
        if (!sessionsGlobalRepository.existsById(sessionId)) {
            throw new EntityNotFoundException("Session not found with id: " + sessionId);
        }
        return groupRepository.findBySessionId(sessionId)
                .stream()
                .map(this::groupToDto)
                .collect(Collectors.toList());
    }

    // =================================================================
    // ПРИВАТНЫЕ МЕТОДЫ-ХЕЛПЕРЫ ДЛЯ ПРЕОБРАЗОВАНИЯ В DTO
    // =================================================================

    /**
     * Преобразует сущность Group в GroupResponseDto.
     */
    private GroupDto groupToDto(Group group) {
        if (group == null) {
            return null;
        }

        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setGroupNumber(group.getGroupNumber());
        dto.setCreationDate(group.getCreationDate());

        if (group.getSession() != null) {
            dto.setSessionId(group.getSession().getId());
        }

        return dto;
    }

}