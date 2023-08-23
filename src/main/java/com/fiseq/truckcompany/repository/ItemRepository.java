package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    Optional<List<Item>> findByTruck_TruckModel(TruckModel truckModel);
}
