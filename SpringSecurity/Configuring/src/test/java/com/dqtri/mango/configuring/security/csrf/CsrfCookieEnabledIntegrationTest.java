package com.dqtri.mango.configuring.security.csrf;

import com.dqtri.mango.configuring.controller.SubmissionController;
import com.dqtri.mango.configuring.security.AbstractIntegrationTest;
import com.dqtri.mango.configuring.security.csrf.config.SecurityWithCsrfCookieConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@WebMvcTest(controllers = {SubmissionController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityWithCsrfCookieConfig.class))
public class CsrfCookieEnabledIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void givenNoCsrf_whenAddSubmission_thenForbidden() throws Exception {
        mvc.perform(post("/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenCsrf_whenAddSubmission_thenCreated() throws Exception {
        mvc.perform(post("/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubmissionPayloadJson())
                        .with(mockSubmitterUser())
                        .with(csrf()))
                .andExpect(status().isCreated());
    }
}
