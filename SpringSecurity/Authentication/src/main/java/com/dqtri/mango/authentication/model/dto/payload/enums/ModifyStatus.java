/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.model.dto.payload.enums;

import com.dqtri.mango.authentication.model.enums.Status;

public enum ModifyStatus {
    DRAFT(Status.DRAFT),
    SUBMITTED(Status.SUBMITTED);

    private final Status status;
    ModifyStatus(Status status){
        this.status = status;
    }

    public Status toStatus(){
        return status;
    }
}
