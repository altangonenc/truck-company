package com.fiseq.truckcompany.entities;

import com.fiseq.truckcompany.constants.FreightTerminals;
import com.fiseq.truckcompany.constants.TruckModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "trucks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private UserProfile owner;

    private TruckModel truckModel;

    private boolean onTheJob;

    private FreightTerminals location;
}
