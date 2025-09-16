package com.project.authtemplate.domain.user.exception;

import com.project.authtemplate.domain.user.exception.error.UserError;
import com.project.authtemplate.global.exception.BusinessException;

public class PasswordWrongException extends BusinessException {

    public static final PasswordWrongException EXCEPTION = new PasswordWrongException();

    private PasswordWrongException() {
        super(UserError.PASSWORD_WRONG);
    }

}
