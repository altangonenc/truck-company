package com.fiseq.truckcompany.constants;

public enum GameSuccessMessages {
    SUCCESSFULLY_TAKE_JOB("You successfully got the job."),
    ITEM_SUCCESSFULLY_PLACED_IN_MARKETPLACE("Item successfully place on the marketplace.");

    private final String name;

    GameSuccessMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}
