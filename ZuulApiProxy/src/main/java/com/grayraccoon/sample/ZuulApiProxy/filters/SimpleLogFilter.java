package com.grayraccoon.sample.ZuulApiProxy.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class SimpleLogFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLogFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 80;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Map<String, String> headers = ctx.getZuulRequestHeaders();

        LOGGER.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        LOGGER.debug("Headers: {}", headers);

        return null;
    }

}
