package com.project.authtemplate.domain.user.application.service.impl;

import com.project.authtemplate.domain.user.application.response.UserResponse;
import com.project.authtemplate.domain.user.exception.UserAlreadyExistException;
import com.project.authtemplate.domain.user.exception.UserNotFoundException;
import com.project.authtemplate.domain.user.application.service.UserService;
import com.project.authtemplate.domain.user.client.dto.User;
import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.repository.jpa.UserJpaRepository;
import com.project.authtemplate.global.common.repository.user.UserSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserSecurity userSecurity;

    @Override
    public void save(UserEntity entity) {
        userJpaRepository.save(entity);
    }

    @Override
    public UserResponse getUser() {
        User user = userSecurity.getUser();
        return UserResponse.toUserResponse(user);
    }

    @Override
    public User getUser(String email) {
        return userJpaRepository
                .findByEmail(email)
                .map(User::toUser)
                .orElseThrow(()-> UserNotFoundException.EXCEPTION);
    }

    @Override
    public void checkUserEmail(String email) {
        if (userJpaRepository.findByEmail(email).isPresent()) {
            throw UserAlreadyExistException.EXCEPTION;
        }
    }

}
