package com.dqtri.mango.configuring.security.csrf;

import com.dqtri.mango.configuring.model.dto.SubmissionPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
public abstract class CsrfAbstractIntegrationTest {
    @Autowired
    protected MockMvc mvc;

    protected String createSubmissionPayloadJson() throws JsonProcessingException {
        SubmissionPayload payload = new SubmissionPayload();
        payload.setValue("Document Value");
        return createPayloadJson(payload);
    }

    protected String createPayloadJson(Object value) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(value);
    }

    protected RequestPostProcessor mockSubmitterUser() {
        return user("submitter").password("********").roles("SUBMITTER");
    }
}