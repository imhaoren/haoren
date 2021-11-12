package com.haoren.rpc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlConfig {

    /**
     * 处理further occurrences of HTTP header parsing errors will be logged at DEBUG level
     * 同时，还需要在application.yml设置设置server.tomcat.max-http-post-size值
     */
    @Bean
    public Integer setRfc() {
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "|{}");
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        return 0;
    }
}
