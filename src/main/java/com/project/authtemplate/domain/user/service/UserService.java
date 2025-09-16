package com.project.authtemplate.domain.user.service;

import com.project.authtemplate.domain.user.client.dto.User;
import com.project.authtemplate.domain.user.domain.entity.UserEntity;

public interface UserService {

    void save(UserEntity entity);

    User getUser();

    User getUser(String email);

    void checkUserEmail(String email);

}
