package com.dqtri.mango.configuring.security.csrf;

import com.dqtri.mango.configuring.security.csrf.config.SecurityWithCsrfCookieConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = { SecurityWithCsrfCookieConfig.class })
public class CsrfCookieEnabledIntegrationTest extends CsrfAbstractIntegrationTest {
    @Nested
    class TestClass{
        @Test
        public void givenNoCsrf_whenAddFoo_thenForbidden() throws Exception {
            // @formatter:off
            mvc
                    .perform(post("/auth/foos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createFoo())
                            .with(testUser()))
                    .andExpect(status().isForbidden());
            // @formatter:on
        }

        @Test
        public void givenCsrf_whenAddFoo_thenCreated() throws Exception {
            // @formatter:off
            mvc
                    .perform(post("/auth/foos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createFoo())
                            .with(testUser())
                            .with(csrf()))
                    .andExpect(status().isCreated());
            // @formatter:on
        }
    }
}
