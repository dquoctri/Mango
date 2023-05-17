package com.dqtri.mango.authentication.model.dto;

import io.jsonwebtoken.lang.Collections;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Setter
@Getter
public final class PageCriteria {
    @Min(0)
    private int pageNumber = 0;
    @Min(1)
    private int pageSize = 25;

    public Pageable toPageable(String... sortProperties) {
        Sort sort = Sort.by("pk");
        if (!Collections.isEmpty(List.of(sortProperties))) {
            sort = Sort.by(sortProperties);
        }
        return PageRequest.of(pageSize, pageNumber, sort);
    }
}
