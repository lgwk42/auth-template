package com.project.authtemplate.domain.user.application.service;

import com.project.authtemplate.domain.user.application.response.UserResponse;
import com.project.authtemplate.domain.user.client.dto.User;
import com.project.authtemplate.domain.user.domain.entity.UserEntity;

public interface UserService {

    void save(UserEntity entity);

    UserResponse getUser();

    User getUser(String email);

    void checkUserEmail(String email);

}
