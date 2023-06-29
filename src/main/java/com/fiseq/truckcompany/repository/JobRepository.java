package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository <Job,Long> {
}
