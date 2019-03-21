package com.grayraccoon.sample.ZuulApiProxy.config;

import com.grayraccoon.sample.ZuulApiProxy.filters.SimpleLogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiltersConfig {

    @Bean
    public SimpleLogFilter simpleLogFilter() {
        return new SimpleLogFilter();
    }

}
