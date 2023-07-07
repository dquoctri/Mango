/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;


public class Helper {
    private Helper(){}

    public static <T> Page<T> createPagination(List<T> content, Page<?> page) {
        return new PageImpl<>(content,
                PageRequest.of(page.getNumber(), page.getSize(), page.getSort()),
                page.getTotalElements());
    }
}
