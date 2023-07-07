/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.repository;

import com.dqtri.mango.submission.model.Submission;
import com.dqtri.mango.submission.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllBySubmitterEmail(String email);
    @Query("SELECT s FROM Submission s " +
            "WHERE :status is null or s.status = :status " +
            "AND :email is null or s.submitter.email = :email")
    Page<Submission> findByStatus(@Param("status") Status status,
                                  @Param("email") String email,
                                  Pageable pageable);
}
