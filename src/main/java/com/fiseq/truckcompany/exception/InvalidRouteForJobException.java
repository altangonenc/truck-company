package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class InvalidRouteForJobException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final GameErrorMessages gameErrorMessages;
}
