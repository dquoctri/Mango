/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.configuring.model.dto;

import com.dqtri.mango.configuring.model.Submission;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmissionPayload {

    @NotBlank
    private String value;

    public Submission toSubmission(){
        Submission submission = new Submission();
        submission.setValue(this.value);
        return submission;
    }
}
