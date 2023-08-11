package com.fiseq.truckcompany.exception;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import com.fiseq.truckcompany.dto.RemainingTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JobIsNotFinishedException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final GameErrorMessages gameErrorMessages;
    private final RemainingTime remainingTime;

    public JobIsNotFinishedException(RemainingTime remainingTime, GameErrorMessages gameErrorMessages) {
        this.remainingTime = remainingTime;
        this.gameErrorMessages = gameErrorMessages;
    }
    public JobIsNotFinishedException(GameErrorMessages gameErrorMessages) {
        this.gameErrorMessages = gameErrorMessages;
        this.remainingTime = null;
    }

}
