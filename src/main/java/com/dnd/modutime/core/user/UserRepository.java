package com.dnd.modutime.core.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByEmailAndProvider(String email, OAuth2Provider provider);

    @Query("select u.createdAt from User u where u.createdAt >= :from")
    List<LocalDateTime> findCreatedAtsAfter(@Param("from") LocalDateTime from);
}
