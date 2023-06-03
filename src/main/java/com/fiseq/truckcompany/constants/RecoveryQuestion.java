package com.fiseq.truckcompany.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecoveryQuestion {
    QUESTION1(1,"What is the name of your first pet?"),
    QUESTION2(2,"In which city were you born?"),
    QUESTION3(3,"What is your mother's maiden name?"),
    QUESTION4(4,"What is the name of your favorite teacher?"),
    QUESTION5(5,"What is the model of your first car?"),
    QUESTION6(6,"What is your favorite book?"),
    QUESTION7(7,"What is the name of the street you grew up on?"),
    QUESTION8(8,"What is your favorite movie?"),
    QUESTION9(9,"What is the name of your childhood best friend?"),
    QUESTION10(10,"What is the make of your first ever computer?");

    private Integer id;
    private String question;

    public static String getRecoveryQuestionById(int id) {
        for (RecoveryQuestion question : RecoveryQuestion.values()) {
            if (question.getId() == id ) {
                return question.getQuestion();
            }
        }
        throw new IllegalArgumentException("Provided recovery question" + id + " is not valid");
    }
}
