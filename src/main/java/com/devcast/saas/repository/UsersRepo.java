package com.devcast.saas.repository;

import com.devcast.saas.model.Users;
import com.devcast.saas.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE u.email = :emailOrUsername OR u.username = :emailOrUsername")
    Optional<Users> findByEmailOrUsername(@Param("emailOrUsername") String emailOrUsername);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM Users u WHERE u.status = :status")
    List<Users> findByStatus(@Param("status") UserStatus status);

    @Query("SELECT u FROM Users u WHERE u.status != 'DELETED'")
    List<Users> findAllActive();

    @Query("SELECT u FROM Users u WHERE u.email LIKE %:searchTerm% OR u.username LIKE %:searchTerm% OR u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm%")
    List<Users> searchUsers(@Param("searchTerm") String searchTerm);
}
