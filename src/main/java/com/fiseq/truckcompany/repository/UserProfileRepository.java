package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUser(User user);

    @Query("SELECT up FROM UserProfile up ORDER BY up.totalMoney DESC")
    List<UserProfile> findAllByOrderByTotalMoneyDesc();

}