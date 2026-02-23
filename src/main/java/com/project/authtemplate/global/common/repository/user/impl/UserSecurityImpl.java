package com.project.authtemplate.global.common.repository.user.impl;

import com.project.authtemplate.domain.user.domain.model.User;
import com.project.authtemplate.global.common.repository.user.UserSecurity;
import com.project.authtemplate.global.security.auth.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityImpl implements UserSecurity {

    @Override
    public User getUser() {
        return ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUser();
    }

    @Override
    public User getUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            return null;
        }
        return details.getUser();
    }

}
