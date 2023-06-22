package com.fiseq.truckcompany.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile implements Serializable {
    @Id
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Job> jobs;

}

