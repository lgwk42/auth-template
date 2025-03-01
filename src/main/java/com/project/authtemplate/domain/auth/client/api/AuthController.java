package com.project.authtemplate.domain.auth.client.api;

import com.project.authtemplate.domain.auth.client.request.RefreshTokenRequest;
import com.project.authtemplate.domain.auth.client.request.SignInRequest;
import com.project.authtemplate.domain.auth.client.request.SignUpRequest;
import com.project.authtemplate.domain.auth.service.AuthService;
import com.project.authtemplate.domain.auth.service.response.JsonWebTokenResponse;
import com.project.authtemplate.domain.auth.service.response.RefreshTokenResponse;
import com.project.authtemplate.global.common.response.BaseResponse;
import com.project.authtemplate.global.common.response.BaseResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse signUp(@Valid @RequestBody SignUpRequest request){
        authService.signUp(request);
        return BaseResponseData.created("회원가입 성공");
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인")
    public BaseResponseData<JsonWebTokenResponse> signIn(@Valid @RequestBody SignInRequest request){
        return BaseResponseData.ok(
                "로그인 성공",
                authService.signIn(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급")
    public BaseResponseData<RefreshTokenResponse> refresh(RefreshTokenRequest request){
        return BaseResponseData.ok(
                "재발급 성공",
                authService.refresh(request.refreshToken()));
    }

}
