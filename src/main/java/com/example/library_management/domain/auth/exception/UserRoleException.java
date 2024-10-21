package com.example.library_management.domain.auth.exception;

import com.example.library_management.domain.common.exception.GlobalException;

import static com.example.library_management.domain.common.exception.GlobalExceptionConst.UNAUTHORIZED_OWNERTOKEN;

public class UserRoleException extends GlobalException {
    public UserRoleException () {
        super(UNAUTHORIZED_OWNERTOKEN);
    }
}
