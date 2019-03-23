package com.grayraccoon.sample.uigateway.config;

import com.grayraccoon.sample.uigateway.filters.SimpleLogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class FiltersConfig {

    @Bean
    @Order(16)
    public SimpleLogFilter simpleLogFilter() {
        return new SimpleLogFilter();
    }

}
