package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.entities.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<Truck,Long> {
    Optional<Truck> findByIdAndOnTheJob(long id, boolean isOnTheJob);
}
