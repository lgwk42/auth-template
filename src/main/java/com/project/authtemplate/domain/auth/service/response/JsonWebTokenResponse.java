package com.project.authtemplate.domain.auth.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record JsonWebTokenResponse (

    String accessToken,
    String refreshToken

){}