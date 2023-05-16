/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISO8601DateSerializer extends JsonSerializer<Date> {
    public static final String DATE_FORMAT = "YYYY-MM-DDThh:mm:ss.sTZD";

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Assert.notNull(value, "NULL is not allowed.");
        gen.writeString(new SimpleDateFormat(DATE_FORMAT).format(value));
    }
}
