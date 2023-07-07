package com.dqtri.mango.authentication;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {AuthenticationApplication.class})
class AuthenticationApplicationTests {
    @Test
    void contextLoads() {
        Assert.isTrue(true);
    }
}
