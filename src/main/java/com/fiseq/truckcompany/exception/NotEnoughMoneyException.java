package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotEnoughMoneyException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final GameErrorMessages gameErrorMessages = GameErrorMessages.USERS_MONEY_IS_NOT_ENOUGH_TO_BUY_THIS_TRUCK;
}
