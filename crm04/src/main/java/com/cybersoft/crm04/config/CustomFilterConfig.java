package com.cybersoft.crm04.config;

import com.cybersoft.crm04.filter.AuthenticationFilter;
import com.cybersoft.crm04.filter.LoginFilter;
import com.cybersoft.crm04.filter.RoleFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // khai báo class này chạy ở tầng config
public class CustomFilterConfig {

    // Khai báo thông tin cấu hình (đăng ký Bean) cho LoginFilter
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilterRegistrationBean() {
        FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginFilter());
        registrationBean.addUrlPatterns("/login"); // khi client gọi link /login thì mới kích hoạt LoginFilter
        registrationBean.setOrder(2);

        return registrationBean;
    }

    // Khai báo thông tin cấu hình (đăng ký Bean) cho AuthenticationFilter
    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenFilterRegistrationBean() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthenticationFilter());
        registrationBean.addUrlPatterns("/role/*", "/user/*", "/job/*", "/task/*"); // khi client gọi 1 trong các link thì mới kích hoạt AuthenticationFilter
        registrationBean.setOrder(1); // set thứ tự cho filter

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> roleFilterRegistrationBean() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter());
        registrationBean.addUrlPatterns("/role/*", "/user/*", "/job/*", "/task/*"); // khi client gọi 1 trong các link thì mới kích hoạt RoleFilter
        registrationBean.setOrder(3); // set thứ tự cho filter

        return registrationBean;
    }

}