package com.project.authtemplate.domain.auth.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonWebTokenResponse {

    private String accessToken;
    private String refreshToken;

}
