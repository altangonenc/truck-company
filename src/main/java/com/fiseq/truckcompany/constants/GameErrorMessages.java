package com.fiseq.truckcompany.constants;

public enum GameErrorMessages {

    GIVEN_TRUCK_MODEL_NOT_FOUND("Given truck model is not valid.");

    private final String name;

    GameErrorMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}