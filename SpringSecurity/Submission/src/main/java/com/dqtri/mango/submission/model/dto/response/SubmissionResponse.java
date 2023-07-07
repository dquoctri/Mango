/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.model.dto.response;

import com.dqtri.mango.submission.model.Submission;
import com.dqtri.mango.submission.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionResponse {
    private long id;
    private String content;
    private Status status;
    private UserResponse submitter;
    private UserResponse assignedUser;
    private String comment;

    public SubmissionResponse(Submission submission) {
        this.id = submission.getPk();
        this.content = submission.getContent();
        this.status = submission.getStatus();
        this.submitter = new UserResponse(submission.getSubmitter());
        if (submission.getAssignedUser() != null) {
            this.assignedUser = new UserResponse(submission.getAssignedUser());
        }
        this.comment = submission.getComment();
    }

    public static List<SubmissionResponse> buildFromSubmissions(List<Submission> submissions) {
        return submissions.stream().map(SubmissionResponse::new).toList();
    }
}
