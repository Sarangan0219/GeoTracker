package com.geotracker.repository;

import com.geotracker.model.auth.Role;
import com.geotracker.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsernameAndRole(String userName, Role role);

    Optional<User> findByUsername(String userName);

}
