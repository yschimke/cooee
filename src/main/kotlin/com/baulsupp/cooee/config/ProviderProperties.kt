package com.baulsupp.cooee.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "provider")
class ProviderProperties {
	var secrets: Map<String, String> = mapOf()
}