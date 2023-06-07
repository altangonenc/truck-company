package com.fiseq.truckcompany.repository;

import com.fiseq.truckcompany.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    boolean existsByUserName(String username);
    User findByUserName(String username);
}
