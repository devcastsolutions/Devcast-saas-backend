package com.devcast.saas.repository;

import com.devcast.saas.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r WHERE r.name IN :roleNames")
    Set<Role> findByNames(@Param("roleNames") Set<String> roleNames);

    boolean existsByName(String name);
}
