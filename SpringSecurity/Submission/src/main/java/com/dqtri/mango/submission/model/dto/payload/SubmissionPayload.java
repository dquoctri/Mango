/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.model.dto.payload;

import com.dqtri.mango.submission.model.Submission;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmissionPayload {
    @NotBlank
    private String subject;

    public Submission toSubmission(){
        Submission submission = new Submission();
        submission.setSubject(this.subject);
        return submission;
    }
}