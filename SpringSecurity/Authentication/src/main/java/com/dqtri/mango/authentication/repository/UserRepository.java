/*
 * Copyright (c) 2023 Deadline
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.repository;

import com.dqtri.mango.authentication.model.MangoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<MangoUser, Long> {
    Optional<MangoUser> findByEmail(String email);
}
