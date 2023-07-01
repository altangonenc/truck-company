package com.fiseq.truckcompany.entities;

import com.fiseq.truckcompany.constants.FreightTerminals;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "jobs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private UserProfile owner;

    private Double charge;

    private FreightTerminals originationTerminal;

    private FreightTerminals destinationTerminal;

}
