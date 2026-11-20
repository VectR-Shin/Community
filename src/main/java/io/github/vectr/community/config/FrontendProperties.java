package io.github.vectr.community.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "community.frontend")
public record FrontendProperties (String url) {}
