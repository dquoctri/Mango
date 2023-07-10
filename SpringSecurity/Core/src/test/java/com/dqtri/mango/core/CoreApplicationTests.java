package com.dqtri.mango.core;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CoreApplication.class})
class CoreApplicationTests {
    @Test
    void contextLoads() {
        Assert.isTrue(true);
    }
}
