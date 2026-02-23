package com.project.authtemplate.global.common.repository.user;

import com.project.authtemplate.domain.user.domain.model.User;

public interface UserSecurity {

    User getUser();

    User getUserOrNull();

}
