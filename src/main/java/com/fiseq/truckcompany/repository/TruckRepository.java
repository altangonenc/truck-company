package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.entities.Truck;
import com.fiseq.truckcompany.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<Truck,Long> {
    Optional<Truck> findByIsUnavailableAndId(boolean isUnavailable, Long id);
    Optional<ArrayList<Truck>> findAllByOwner(UserProfile userProfile);
    Optional<Truck> findByOwnerAndIdAndIsUnavailable(UserProfile userProfile, Long id, boolean isUnavailable);
}
