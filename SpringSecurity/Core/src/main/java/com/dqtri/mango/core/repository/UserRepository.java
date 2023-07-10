/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.repository;

import com.dqtri.mango.core.model.SafeguardUser;
import com.dqtri.mango.core.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SafeguardUser, Long> {
    Optional<SafeguardUser> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM SafeguardUser u WHERE :role is null or u.role = :role")
    Page<SafeguardUser> findByRole(@Param("role") Role role, Pageable pageable);
}
