package com.fiseq.truckcompany.constants;

public enum GameSuccessMessages {
    SUCCESSFULLY_TAKE_JOB("You successfully got the job.");

    private final String name;

    GameSuccessMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}
