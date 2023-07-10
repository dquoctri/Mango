/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "415", description = "Content-Type '<content-type>' is not supported.",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ProblemDetail.class))}),
        @ApiResponse(responseCode = "406", description = "The server cannot produce a response matching the list of acceptable values",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ProblemDetail.class))}),
})
public @interface GlobalApiResponses {
}
