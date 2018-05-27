package com.siteminder.challenge.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class EmailParams {
    @JsonProperty("from")
    private Email from;
    
    @JsonProperty("to")
    private List<Email> to;
    
    @JsonProperty("cc")
    private List<Email> cc;
    
    @JsonProperty("bcc")
    private List<Email> bcc;
    
    @JsonProperty("subject")
    private String subject;
    
    @JsonProperty("message")
    private String message; 
    
    public EmailParams() {
    }
    
    @JsonProperty("from")
    public Email getFrom() {
        return from;
    }
    public void setFrom(Email from) {
        this.from = from;
    }
    
    @JsonProperty("to")
    public List<Email> getTo() {
        return to;
    }
    public void setTo(List<Email> to) {
        this.to = to;
    }
    
    @JsonProperty("cc") 
    public List<Email> getCc() {
        return cc;
    }
    public void setCc(List<Email> cc) {
        this.cc = cc;
    }

    @JsonProperty("bcc")
    public List<Email> getBcc() {
        return bcc;
    }
    public void setBcc(List<Email> bcc) {
        this.bcc = bcc;
    }
    
    @JsonProperty("subject")
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}