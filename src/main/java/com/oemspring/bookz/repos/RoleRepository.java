package com.oemspring.bookz.repos;

import com.oemspring.bookz.models.ERole;
import com.oemspring.bookz.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
