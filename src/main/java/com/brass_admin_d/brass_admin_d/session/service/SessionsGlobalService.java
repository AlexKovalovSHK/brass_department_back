package com.brass_admin_d.brass_admin_d.session.service;

import com.brass_admin_d.brass_admin_d.session.dto.GlobalSessionsDto;
import com.brass_admin_d.brass_admin_d.session.dto.NewGlobalSessionsDto;

public interface SessionsGlobalService {
    GlobalSessionsDto createSession(NewGlobalSessionsDto requestDto);
    GlobalSessionsDto getSessionById(Long id);
    Iterable<GlobalSessionsDto> getAllSessions();
    GlobalSessionsDto updateSession(Long id, GlobalSessionsDto requestDto);
    Boolean deleteSession(Long id);
}