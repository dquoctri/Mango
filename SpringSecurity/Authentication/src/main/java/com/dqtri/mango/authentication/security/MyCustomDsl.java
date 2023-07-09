/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.authentication.security;

import com.dqtri.mango.authentication.security.access.AccessAuthenticationFilter;
import com.dqtri.mango.authentication.security.refresh.RefreshAuthenticationFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Setter
@Getter
public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        // any method that adds another configurer
        // must be done in the init method
        http.csrf(AbstractHttpConfigurer::disable);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        // https://docs.spring.io/spring-security/site/docs/4.2.1.RELEASE/reference/htmlsingle/#filter-ordering
        // here we lookup from the ApplicationContext. You can also just create a new instance.
        RefreshAuthenticationFilter myFilter = context.getBean(RefreshAuthenticationFilter.class);
        AccessAuthenticationFilter myFilter2 = context.getBean(AccessAuthenticationFilter.class);
        http.addFilterBefore(myFilter2, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(myFilter, AccessAuthenticationFilter.class);
    }

    public static MyCustomDsl customDsl() {
        return new MyCustomDsl();
    }
}
