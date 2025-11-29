package com.interview.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "logging.request-filter")
public class RequestLoggingConfig {

	private List<String> excludedPaths = new ArrayList<>();

	public List<String> getExcludedPaths() {
		return excludedPaths;
	}

	public void setExcludedPaths(List<String> excludedPaths) {
		this.excludedPaths = excludedPaths;
	}
}
