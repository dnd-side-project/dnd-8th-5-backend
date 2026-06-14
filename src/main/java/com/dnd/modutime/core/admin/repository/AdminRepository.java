package com.dnd.modutime.core.admin.repository;

import com.dnd.modutime.core.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByRefreshToken(String refreshToken);
}
