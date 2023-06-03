package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ChangePasswordException extends Exception{
    private final HttpStatus httpStatus;
    private final UserRegistrationErrorMessages userRegistrationErrorMessages;
}
