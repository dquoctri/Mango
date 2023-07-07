/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.model.dto.payload;

import com.dqtri.mango.authentication.model.Submission;
import com.dqtri.mango.authentication.model.dto.payload.enums.ModifyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@ToString
public class SubmissionPayload {
    @NotBlank
    @Length(max = 152)
    private String content;

    @NotNull
    @Schema(example = "DRAFT")
    private ModifyStatus status = ModifyStatus.DRAFT;

    public Submission toSubmission() {
        Submission submission = new Submission();
        submission.setContent(this.content);
        submission.setStatus(this.status.toStatus());
        return submission;
    }
}
