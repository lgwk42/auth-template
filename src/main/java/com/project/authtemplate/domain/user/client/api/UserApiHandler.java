package com.project.authtemplate.domain.user.client.api;

import com.project.authtemplate.domain.user.application.response.UserResponse;
import com.project.authtemplate.domain.user.application.usecase.UserUseCase;
import com.project.authtemplate.global.common.dto.response.BaseResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserApiHandler {

    private final UserUseCase userUseCase;

    /**
     * 내 정보 조회(토큰기반) API
     * @return BaseResponseData UserResponse
     */
    @GetMapping
    public BaseResponseData<UserResponse> getUserByToken() {
        return BaseResponseData.ok(
                "조회 성공",
                userUseCase.getMyInfo()
        );
    }

}
