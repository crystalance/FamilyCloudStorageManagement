package com.example.familycloudstoragemanagement.UserManagement.mysecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


//这段代码使用了 Spring Security 来保护应用程序，它的作用如下：
//所有的请求都被允许，即使没有认证也可以访问应用程序，这样可以确保应用程序的基本可访问性。
//但是，对于需要认证的请求，必须授权才能访问，这样可以确保只有授权用户才能访问应用程序的受保护资源。
//禁用了 CSRF 保护，这样可以方便地进行开发和测试。
//如果不使用这段代码，可能会导致应用程序的安全性受到威胁。例如，未经认证的用户可以访问应用程序的受保护资源，可能会导致机密信息泄露或者应用程序被攻击。因此，使用 Spring Security 来保护应用程序是非常重要的
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();

    }

}
