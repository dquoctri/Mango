/*
 * Copyright (c) 2023 Deadline
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.repository;

import com.dqtri.mango.submission.model.SubmissionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SubmissionUser, Long> {
    Optional<SubmissionUser> findByEmail(String email);
}
