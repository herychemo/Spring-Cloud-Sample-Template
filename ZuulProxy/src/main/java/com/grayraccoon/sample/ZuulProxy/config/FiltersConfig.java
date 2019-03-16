package com.grayraccoon.sample.ZuulProxy.config;

import com.grayraccoon.sample.ZuulProxy.filters.SimpleLogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiltersConfig {

    @Bean
    public SimpleLogFilter simpleLogFilter() {
        return new SimpleLogFilter();
    }

}
