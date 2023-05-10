package com.dqtri.mango.configuring.security.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = { SecurityWithCsrfConfig.class })
public class CsrfEnabledIntegrationTest extends CsrfAbstractIntegrationTest {
    @Test
    public void givenNoCsrf_whenAddFoo_thenForbidden() throws Exception {
        mvc.perform(
                post("/foos").contentType(MediaType.APPLICATION_JSON)
                        .content(createFoo())
                        .with(testUser())
        ).andExpect(status().isForbidden());
    }

    @Test
    public void givenCsrf_whenAddFoo_thenCreated() throws Exception {
        mvc.perform(
                post("/foos").contentType(MediaType.APPLICATION_JSON)
                        .content(createFoo())
                        .with(testUser()).with(csrf())
        ).andExpect(status().isCreated());
    }
}
