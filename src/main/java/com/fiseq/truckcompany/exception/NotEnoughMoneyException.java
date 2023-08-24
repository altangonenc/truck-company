package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class NotEnoughMoneyException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final GameErrorMessages gameErrorMessages;
}
