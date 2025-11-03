package com.project.authtemplate.global.common.dto.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ErrorResponse(int status, String message) {

    public static ErrorResponse of(String message) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

}
