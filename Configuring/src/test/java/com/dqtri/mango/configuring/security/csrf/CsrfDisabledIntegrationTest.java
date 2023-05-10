package com.dqtri.mango.configuring.security.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = { SecurityWithoutCsrfConfig.class})
public class CsrfDisabledIntegrationTest extends CsrfAbstractIntegrationTest {


    @Test
    public void givenNotAuth_whenAddFoo_thenUnauthorized() throws Exception {
        mvc.perform(
                post("/foos").contentType(MediaType.APPLICATION_JSON)
                        .content(createFoo())
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAuth_whenAddFoo_thenCreated() throws Exception {
        mvc.perform(
                post("/foos").contentType(MediaType.APPLICATION_JSON)
                        .content(createFoo())
                        .with(testUser())
        ).andExpect(status().isCreated());
    }
}
