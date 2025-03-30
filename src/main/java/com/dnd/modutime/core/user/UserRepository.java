package com.dnd.modutime.core.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByEmailAndProvider(String email, OAuth2Provider provider);
}
