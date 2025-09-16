package com.project.authtemplate.global.common.repository.user.impl;

import com.project.authtemplate.domain.user.client.dto.User;
import com.project.authtemplate.global.common.repository.user.UserSecurity;
import com.project.authtemplate.global.security.auth.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserSecurityImpl implements UserSecurity {

    @Override
    public User getUser() {
        return ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUser();
    }

}
