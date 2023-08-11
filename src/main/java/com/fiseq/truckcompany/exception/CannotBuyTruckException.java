package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CannotBuyTruckException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final GameErrorMessages gameErrorMessages = GameErrorMessages.SOMETHING_WENT_WRONG_WHILE_BUY_TRUCK;
}
