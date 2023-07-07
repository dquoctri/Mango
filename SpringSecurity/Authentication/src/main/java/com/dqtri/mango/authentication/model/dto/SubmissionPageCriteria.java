/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.model.dto;

import com.dqtri.mango.authentication.model.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionPageCriteria extends PageCriteria {
    @Schema(example = "AWAITING_APPROVAL")
    private Status status;
    @Schema(example = "mango@dqtri.com")
    private String submitterEmail;
}
