package com.dqtri.mango.configuring.security.csrf;

import com.dqtri.mango.configuring.controller.AuthController;
import com.dqtri.mango.configuring.controller.UserController;
import com.dqtri.mango.configuring.model.dto.LoginPayload;
import com.dqtri.mango.configuring.security.AbstractIntegrationTest;
import com.dqtri.mango.configuring.security.csrf.config.ManualSecurityCsrfConfig;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@WebMvcTest(controllers = {AuthController.class, UserController.class},
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ManualSecurityCsrfConfig.class))
public class CsrfManualSecurityIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void givenNoSession_whenGetUserMe_thenUnauthorized() throws Exception {
        mvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenSession_whenGetUserMe_thenOk() throws Exception {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setEmail("submitter@mango.dqtri.com");
        loginPayload.setPassword("submitter");
        HttpSession session = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayloadJson(loginPayload)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession();

        assert session != null;
        mvc.perform(get("/users/me").session((MockHttpSession) session))
                .andExpect(status().is2xxSuccessful());
    }
}
