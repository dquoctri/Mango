/*
 * Copyright (c) 2023 Deadline
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.repositories;

import com.dqtri.mango.authentication.models.DlnUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DlnUserRepository extends JpaRepository<DlnUser, Long> {
    Optional<DlnUser> findByEmail(String email);
}
