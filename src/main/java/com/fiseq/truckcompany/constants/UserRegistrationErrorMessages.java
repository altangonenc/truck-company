package com.fiseq.truckcompany.constants;

public enum UserRegistrationErrorMessages {
    EMAIL_ALREADY_EXISTS("Given email already exists."),
    USERNAME_ALREADY_EXIST("Given Username already exists. Please choose another one.");

    private final String name;

    UserRegistrationErrorMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}
