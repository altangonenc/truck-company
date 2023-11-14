package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import org.springframework.http.HttpStatus;

public class CannotSellTruckException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final GameErrorMessages gameErrorMessages;

    public CannotSellTruckException(GameErrorMessages gameErrorMessages) {
        this.gameErrorMessages = gameErrorMessages;
    }
}
