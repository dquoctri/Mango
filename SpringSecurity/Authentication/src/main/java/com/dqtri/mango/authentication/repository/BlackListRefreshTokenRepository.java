/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.repository;

import com.dqtri.mango.authentication.model.BlackListRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRefreshTokenRepository extends JpaRepository<BlackListRefreshToken, Long> {
    boolean existsByToken(String token);
}

