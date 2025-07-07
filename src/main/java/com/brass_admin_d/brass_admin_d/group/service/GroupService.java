package com.brass_admin_d.brass_admin_d.group.service;

import com.brass_admin_d.brass_admin_d.group.dto.GroupDto;
import com.brass_admin_d.brass_admin_d.group.dto.NewGroupDto;

public interface GroupService {
    GroupDto createGroup(NewGroupDto groupDto);
    GroupDto getGroupById(Long id);
    Boolean deleteGroupById(Long id);
    Iterable<GroupDto> getAllGroups();
    Iterable<GroupDto> getGroupBySessionId(Long sessionId);
}
