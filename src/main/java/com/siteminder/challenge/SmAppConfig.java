package com.siteminder.challenge;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/*
 * Intended to load all application configurations from application.properties
 */
@Configuration
@ConfigurationProperties("smchallenge")
@Validated
public class SmAppConfig {
	@NotEmpty
	private List<SmMailServiceConfig> smMailServiceConfig;

	public List<SmMailServiceConfig> getSmMailServiceConfig() {
		return smMailServiceConfig;
	}

	public void setSmMailServiceConfig(List<SmMailServiceConfig> smMailServiceConfig) {
		this.smMailServiceConfig = smMailServiceConfig;
	}
}
