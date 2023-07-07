package com.fiseq.truckcompany.constants;

public enum GameErrorMessages {

    GIVEN_TRUCK_MODEL_NOT_FOUND("Given truck model is not valid."),
    SOMETHING_WENT_WRONG_WHILE_BUY_TRUCK("Something went wrong while you try to bought a truck.");

    private final String name;

    GameErrorMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}