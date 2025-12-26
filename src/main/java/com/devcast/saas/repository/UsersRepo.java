package com.devcast.saas.repository;

import com.devcast.saas.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo  extends JpaRepository<Long, Users> {

    // Combined email/username finder
    @Query("SELECT u FROM Users u WHERE u.email = :emailOrUsername OR u.username = :emailOrUsername")
    Optional<Users> findByEmailOrUsername(@Param("emailOrUsername") String emailOrUsername);
}
