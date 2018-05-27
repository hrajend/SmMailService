package com.siteminder.challenge;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/*
 * Intended to load mailing service configurations from application.properties
 */
public class SmMailServiceConfig {
    // Only sendgrid and mailgun are supported as of now.
    // TODO: Do this in a better way. Don't hard-code it.
    @Pattern(regexp = "(?i)sendgrid|mailgun")
    private String serviceName; 

    @NotBlank
    private String endPoint;
    
    @NotBlank
    private String apiKey;
    
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }   
}
