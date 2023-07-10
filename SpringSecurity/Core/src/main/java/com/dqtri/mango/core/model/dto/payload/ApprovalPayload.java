/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.model.dto.payload;

import com.dqtri.mango.core.model.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@ToString
public class ApprovalPayload {
    @NotNull
    Action action;

    @Length(min = 0, max = 300)
    String comment;

    public Status getStatus(){
        if (Action.REJECT.equals(action)) return Status.REJECTED;
        return Status.APPROVED;
    }
}
