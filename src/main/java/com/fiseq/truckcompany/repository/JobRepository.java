package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.constants.FreightTerminals;
import com.fiseq.truckcompany.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository <Job,Long> {
    int countByOriginationTerminal(FreightTerminals originationTerminal);
    List<Job> findAllByOriginationTerminal(FreightTerminals freightTerminal);
}
