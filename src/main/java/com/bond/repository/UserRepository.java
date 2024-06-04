package com.bond.repository;

import com.bond.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User user LEFT JOIN FETCH user.roles WHERE user.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    @Query("FROM User user WHERE user.email = :email")
    Optional<User> findByEmailWithoutRoles(String email);

    @Query("FROM User user LEFT JOIN FETCH user.roles WHERE user.id = :id")
    Optional<User> findByIdWithRoles(Long id);
}
