package com.project.authtemplate.global.common.dto.response;

import com.project.authtemplate.global.exception.error.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class BaseResponse {

    private int status;
    private String message;

    public static BaseResponse of(HttpStatus status, String message) {
        return new BaseResponse(status.value(), message);
    }

    public static BaseResponse of(ErrorCode errorCode) {
        return BaseResponse.builder()
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .build();
    }


    public static BaseResponse ok(String message) {
        return new BaseResponse(HttpStatus.OK.value(), message);
    }

    public static BaseResponse created(String message) {
        return new BaseResponse(HttpStatus.CREATED.value(), message);
    }

}
