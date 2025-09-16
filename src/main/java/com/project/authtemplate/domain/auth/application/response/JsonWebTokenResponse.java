package com.project.authtemplate.domain.auth.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record JsonWebTokenResponse (

    String accessToken,
    String refreshToken

){}