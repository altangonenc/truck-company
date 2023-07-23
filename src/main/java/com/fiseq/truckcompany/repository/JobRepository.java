package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.constants.FreightTerminals;
import com.fiseq.truckcompany.entities.Job;
import com.fiseq.truckcompany.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository <Job,Long> {
    int countByOriginationTerminal(FreightTerminals originationTerminal);
    List<Job> findAllByOriginationTerminal(FreightTerminals freightTerminal);
    Optional<Job> findByIdAndOwnerEquals(long id, UserProfile userProfile);
}
