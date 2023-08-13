package com.fiseq.truckcompany.constants;

public enum GameErrorMessages {

    GIVEN_TRUCK_MODEL_OR_TERMINAL_NOT_FOUND("Given truck model or terminal is not valid."),
    GIVEN_TRUCK_MODEL_NOT_FOUND("Given truck model is not valid."),
    SOMETHING_WENT_WRONG_WHILE_BUY_TRUCK("Something went wrong while you try to bought a truck."),
    USERS_MONEY_IS_NOT_ENOUGH_TO_BUY_THIS_TRUCK("User does not have enough money to buy this truck."),
    GIVEN_TERMINAL_COUNTRY_NAME_NOT_FOUND("Given terminal name is not valid."),
    GIVEN_TERMINAL_NAMES_IN_ROUTE_NOT_VALID("Given route was wrong. Check your specified countries."),
    GIVEN_JOB_ID_OR_TRUCK_ID_INVALID("Given job id or selected truck id is not valid"),
    SOMETHING_WRONG_DIFFERENT_REGION_DISTANCE_CALCULATOR("You have been selected 2 countries that cannot be reached by road."),
    WRONG_ROUTE_FOR_THIS_JOB("Given route is false. Please check it."),
    JOB_IS_NOT_FINISHED("Specified job is not finished yet."),
    JOB_IS_IN_VACANT_STATUS("Job is vacant. You cannot finish a vacant job."),
    THERE_IS_NO_JOB_FOR_USER("There is no job for user."),
    THERE_IS_NO_VACANT_JOB("There is no vacant job."),
    TRUCK_CRASHED("Your truck crashed on road. Sorry to hear that but you cannot take any money for this job.(Don't forget to pay your gas :) )"),
    TRUCK_OUTDATED("Specified truck cannot do this job. Truck is outdated. "),
    USER_HAS_NO_TRUCK("Specified User doesn't have any truck.");
    private final String name;

    GameErrorMessages(String name) {
        this.name = name;
    }

    public String getUserText() {
        return this.name;
    }
}