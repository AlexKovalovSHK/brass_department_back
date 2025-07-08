package com.brass_admin_d.brass_admin_d.security.utils;

import com.brass_admin_d.brass_admin_d.user.dao.UserRepository;
import com.brass_admin_d.brass_admin_d.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CustomSecurity {

    private final UserRepository userRepository;

    public boolean hasUserAccessToUserId(String userName, Integer userId) {
        User userAccount = userRepository.findByUsernameIgnoreCase(userName).orElse(null);
        return userAccount != null && userAccount.getId().equals(userId);
    }

    public boolean isUserAdmin(Supplier<Authentication> authentication) {
        return authentication.get().getAuthorities().contains(new SimpleGrantedAuthority("ADMINISTRATOR"));
    }
}
