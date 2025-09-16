package com.project.authtemplate.domain.auth.client.api;

import com.project.authtemplate.domain.auth.application.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.application.response.RefreshTokenResponse;
import com.project.authtemplate.domain.auth.application.usecase.AuthUseCase;
import com.project.authtemplate.domain.auth.client.request.RefreshTokenRequest;
import com.project.authtemplate.domain.auth.client.request.SignInRequest;
import com.project.authtemplate.domain.auth.client.request.SignUpRequest;
import com.project.authtemplate.global.common.dto.response.BaseResponse;
import com.project.authtemplate.global.common.dto.response.BaseResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthApiHandler {

    private final AuthUseCase authUseCase;

    /**
     * 회원가입 API
     *
     * @param request SignUpRequest
     *
     * @return status, message
     *
     * */
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse signUp(@RequestBody @Valid final SignUpRequest request) {
        authUseCase.signUp(request);
        return BaseResponse.created("회원가입 성공");
    }

    /**
     * 로그인 API
     *
     * @param request email, password
     *
     * @return status, message, data { JsonWebTokenResponse }
     *
     * */
    @PostMapping("/sign-in")
    public BaseResponseData<JsonWebTokenResponse> signIn(@RequestBody @Valid final SignInRequest request) {
        return BaseResponseData.ok(
                "로그인 성공",
                authUseCase.signIn(request)
        );
    }

    /**
     * 토큰 재발급 API
     *
     * @param request RefreshTokenRequest
     *
     * @return status, message, data { RefreshTokenResponse }
     *
     * */
    @PostMapping("/refresh")
    public BaseResponseData<RefreshTokenResponse> refresh(@RequestBody @Valid final RefreshTokenRequest request) {
        return BaseResponseData.ok(
                "재발급 성공",
                authUseCase.refresh(request)
        );
    }

}
