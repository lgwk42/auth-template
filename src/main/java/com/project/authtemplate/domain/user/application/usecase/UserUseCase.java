package com.project.authtemplate.domain.user.application.usecase;

import com.project.authtemplate.domain.user.application.response.UserResponse;
import com.project.authtemplate.domain.user.application.service.UserService;
import com.project.authtemplate.domain.user.domain.model.User;
import com.project.authtemplate.global.common.repository.user.UserSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUseCase {

    private final UserService userService;
    private final UserSecurity userSecurity;

    public UserResponse getMyInfo() {
        log.info("[UserUseCase] getMyInfo");
        User user = userSecurity.getUser();
        return UserResponse.of(user);
    }

}
