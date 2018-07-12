package de.unidue.ltl.recommender.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilterConfig
{

    @Bean
    public CommonsRequestLoggingFilter logFilter()
    {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(1000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("[");
        filter.setAfterMessageSuffix("]");
        return filter;
    }
}