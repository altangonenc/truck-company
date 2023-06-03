package com.fiseq.truckcompany.constants;

public enum UserRegistrationErrorMessages {
    EMAIL_ALREADY_EXISTS("Given email already exists."),
    USERNAME_ALREADY_EXIST("Given Username already exists. Please choose another one."),
    FIELDS_CANNOT_BE_EMPTY("Fields of user cannot be empty."),
    USER_NOT_EXISTS("User is not valid."),
    INVALID_AUTH_PARAMETERS("Auth parameters are invalid."),
    INVALID_RECOVERY_ANSWER("Your recovery question answer was wrong.");

    private final String name;

    UserRegistrationErrorMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}
