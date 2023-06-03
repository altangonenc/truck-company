package com.fiseq.truckcompany.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String userName;

    private String firstName;

    private String lastName;

    private String password;

    private Integer recoveryQuestionId;

    private String recoveryAnswer;
}
