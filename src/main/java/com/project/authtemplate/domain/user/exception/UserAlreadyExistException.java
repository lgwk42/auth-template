package com.project.authtemplate.domain.user.exception;

import com.project.authtemplate.domain.user.exception.error.UserError;
import com.project.authtemplate.global.exception.BusinessException;

public class UserAlreadyExistException extends BusinessException {

    public static final UserAlreadyExistException EXCEPTION = new UserAlreadyExistException();

    public UserAlreadyExistException() {
        super(UserError.USER_ALREADY_EXIST);
    }

}
