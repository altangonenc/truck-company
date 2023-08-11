package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TruckCrashedException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.OK;
    private final GameErrorMessages gameErrorMessages = GameErrorMessages.TRUCK_CRASHED;
}
