/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.submission.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.dqtri.mango.submission.util.PageDeserializer.buildSortFromJsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomPageImpl<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomPageImpl(@JsonProperty("content") List<T> content,
                          @JsonProperty("number") int number,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") Long totalElements,
                          @JsonProperty("sort") JsonNode sort) {
        super(content, PageRequest.of(number, size, buildSortFromJsonNode(sort)), totalElements);
    }

    public CustomPageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CustomPageImpl(List<T> content) {
        super(content);
    }

    public CustomPageImpl() {
        super(new ArrayList<>());
    }

    @Override
    @JsonProperty
    public int getTotalPages() {
        return super.getTotalPages();
    }

    @Override
    @JsonProperty
    public long getTotalElements() {
        return super.getTotalElements();
    }

    @Override
    @JsonProperty
    public int getNumber() {
        return super.getNumber();
    }

    @Override
    @JsonProperty
    public int getSize() {
        return super.getSize();
    }

    @Override
    @JsonProperty
    public int getNumberOfElements() {
        return super.getNumberOfElements();
    }

    @Override
    @JsonProperty
    public List<T> getContent() {
        return super.getContent();
    }

    @Override
    @JsonProperty
    public boolean hasContent() {
        return super.hasContent();
    }

    @Override
    @JsonIgnore
    public Sort getSort() {
        return super.getSort();
    }

    @Override
    @JsonIgnore
    public boolean isFirst() {
        return super.isFirst();
    }

    @Override
    @JsonIgnore
    public boolean isLast() {
        return super.isLast();
    }

    @Override
    @JsonIgnore
    public boolean hasNext() {
        return super.hasNext();
    }

    @Override
    @JsonIgnore
    public boolean hasPrevious() {
        return super.hasPrevious();
    }

    @Override
    @JsonIgnore
    public Pageable nextPageable() {
        return super.nextPageable();
    }

    @Override
    @JsonIgnore
    public Pageable previousPageable() {
        return super.previousPageable();
    }

    @Override
    @JsonIgnore
    public Iterator<T> iterator() {
        return super.iterator();
    }
}
