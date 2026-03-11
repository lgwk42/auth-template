package com.project.authtemplate.domain.user.application.query;

import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.model.User;
import com.project.authtemplate.domain.user.domain.repository.jpa.UserJpaRepository;
import com.project.authtemplate.domain.user.exception.UserAlreadyExistException;
import com.project.authtemplate.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserJpaRepository userJpaRepository;

    public User findByEmail(String email) {
        UserEntity entity = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        return User.of(entity);
    }

    public void checkEmailNotExist(String email) {
        if (userJpaRepository.findByEmail(email).isPresent()) {
            throw UserAlreadyExistException.EXCEPTION;
        }
    }

}
