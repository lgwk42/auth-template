package com.project.authtemplate.domain.user.exception;

import com.project.authtemplate.domain.user.exception.error.UserError;
import com.project.authtemplate.global.exception.BusinessException;

public class UserExistException extends BusinessException {

    public static final UserExistException EXCEPTION = new UserExistException();

    public UserExistException() {
        super(UserError.USER_EXIST);
    }
}
