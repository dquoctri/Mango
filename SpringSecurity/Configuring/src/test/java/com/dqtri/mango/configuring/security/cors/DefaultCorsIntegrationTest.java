package com.dqtri.mango.configuring.security.cors;

import com.dqtri.mango.configuring.controller.SubmissionController;
import com.dqtri.mango.configuring.security.AbstractIntegrationTest;
import com.dqtri.mango.configuring.security.cors.config.DefaultSecurityCorsConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@WebMvcTest(controllers = {SubmissionController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = DefaultSecurityCorsConfig.class))
public class DefaultCorsIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = {"http://www.unknow-origin.com", "http://localhost:4200", "http://localhost:3000"})
    public void givenCrossOrigin_whenAddSubmission_thenForbidden(String origin) throws Exception {
        mvc.perform(
                options("/submissions")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Origin", origin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser())
        ).andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://localhost:9000"})
    public void givenCrossOrigin_whenAddSubmission_thenOk(String origin) throws Exception {
        mvc.perform(
                options("/submissions")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Origin", origin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser())
        ).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE", "PATH"})
    public void givenMethod_whenCallSubmissionOptions_thenOK(String method) throws Exception {
        mvc.perform(
                options("/submissions")
                        .header("Access-Control-Request-Method", method)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser())
        ).andExpect(status().isOk());
    }

    @Test
    public void givenPutMethod_whenCallSubmissionOptions_thenOK() throws Exception {
        mvc.perform(
                put("/submissions/1")
                        .header("Access-Control-Request-Method", "PUT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser())
        ).andExpect(status().isOk());
    }

    @Test
    public void givenAcceptMethod_whenAddSubmission_thenCreated() throws Exception {
        mvc.perform(
                post("/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser())
        ).andExpect(status().isCreated())
                .andExpect(header().stringValues("Vary", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-XSS-Protection", "0"))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Source-Id", "1"));
    }
}
