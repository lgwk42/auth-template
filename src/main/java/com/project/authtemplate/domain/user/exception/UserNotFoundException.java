package com.project.authtemplate.domain.user.exception;

import com.project.authtemplate.domain.user.exception.error.UserError;
import com.project.authtemplate.global.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

    public static final UserNotFoundException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException(){
        super(UserError.USER_NOT_FOUND);
    }

}
