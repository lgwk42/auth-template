package com.project.authtemplate.domain.user.application.service;

import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.domain.model.User;
import com.project.authtemplate.domain.user.domain.repository.jpa.UserJpaRepository;
import com.project.authtemplate.domain.user.exception.UserAlreadyExistException;
import com.project.authtemplate.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;

    public User findByEmail(String email) {
        UserEntity entity = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        return User.of(entity);
    }

    @Transactional
    public void save(String email, String password, String name, UserRole userRole) {
        log.info("[UserService] save - email={}", email);
        UserEntity entity = UserEntity.builder()
                .email(email)
                .password(password)
                .name(name)
                .userRole(userRole)
                .build();
        userJpaRepository.save(entity);
    }

    public void validateEmailNotExist(String email) {
        if (userJpaRepository.findByEmail(email).isPresent()) {
            throw UserAlreadyExistException.EXCEPTION;
        }
    }

}
