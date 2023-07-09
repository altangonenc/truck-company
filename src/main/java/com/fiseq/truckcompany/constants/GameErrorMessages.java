package com.fiseq.truckcompany.constants;

public enum GameErrorMessages {

    GIVEN_TRUCK_MODEL_NOT_FOUND("Given truck model is not valid."),
    SOMETHING_WENT_WRONG_WHILE_BUY_TRUCK("Something went wrong while you try to bought a truck."),
    USERS_MONEY_IS_NOT_ENOUGH_TO_BUY_THIS_TRUCK("User does not have enough money to buy this truck.");

    private final String name;

    GameErrorMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}