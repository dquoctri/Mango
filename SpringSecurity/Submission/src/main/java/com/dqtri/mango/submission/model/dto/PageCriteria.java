/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PageCriteria {
    @Min(0)
    @Schema(example = "0")
    private int pageNumber = 0;

    @Min(1)
    @Schema(example = "25")
    private int pageSize = 25;

    public PageRequest toPageable(String sortProperties) {
        Sort.Order order = Sort.Order.by(sortProperties).with(Sort.Direction.ASC);
        Sort sort = Sort.by(List.of(order));
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
