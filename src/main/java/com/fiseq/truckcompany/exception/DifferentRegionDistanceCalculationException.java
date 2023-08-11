package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import org.springframework.http.HttpStatus;

public class DifferentRegionDistanceCalculationException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final GameErrorMessages gameErrorMessages = GameErrorMessages.SOMETHING_WRONG_DIFFERENT_REGION_DISTANCE_CALCULATOR;
}
