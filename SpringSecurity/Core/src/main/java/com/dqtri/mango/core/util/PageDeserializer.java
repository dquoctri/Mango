/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.core.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class PageDeserializer<T> extends JsonDeserializer<Page<T>> {

    private final Class<T> contentClass;

    @Override
    public Page<T> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = objectMapper.readTree(jsonParser);
        List<T> content = getContent(node.get("content"));
        // Extract the sort information
        Sort sort = buildSortFromJsonNode(node.get("sort"));

        // Extract other fields
        int number = node.get("number").asInt();
        int size = node.get("size").asInt();
        long totalElements = node.get("totalElements").asLong();

        return new PageImpl<>(content, PageRequest.of(number, size, sort), totalElements);
    }

    protected List<T> getContent(JsonNode contentNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> content = new ArrayList<>();
        if (!contentNode.isArray()) {
            return content;
        }
        for (JsonNode elementNode : contentNode) {
            T element = objectMapper.convertValue(elementNode, contentClass);
            content.add(element);
        }
        return content;
    }

    public static Sort buildSortFromJsonNode(JsonNode sortNode) {
        boolean empty = sortNode.get("empty").asBoolean();
        boolean sorted = sortNode.get("sorted").asBoolean();
        boolean unsorted = sortNode.get("unsorted").asBoolean();

        if (empty) return Sort.unsorted();
        if (unsorted) return Sort.unsorted();
        if (sorted) return Sort.by("pk");

        return Sort.unsorted();
    }
}