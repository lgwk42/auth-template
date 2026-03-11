package com.project.authtemplate.domain.user.application.service;

import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import com.project.authtemplate.domain.user.domain.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;

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

}
