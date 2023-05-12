package com.dqtri.mango.configuring.security.csrf;

import com.dqtri.mango.configuring.controller.SubmissionController;
import com.dqtri.mango.configuring.security.csrf.config.SecurityWithoutCsrfConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@WebMvcTest(controllers = {SubmissionController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityWithoutCsrfConfig.class))
public class CsrfDisabledIntegrationTest extends CsrfAbstractIntegrationTest {

    @Test
    public void givenNotAuth_whenAddSubmission_thenUnauthorized() throws Exception {
        mvc.perform(
                post("/submissions").contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAuth_whenAddSubmission_thenCreated() throws Exception {
        mvc.perform(
                post("/submissions").contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser())
        ).andExpect(status().isCreated());
    }
}
