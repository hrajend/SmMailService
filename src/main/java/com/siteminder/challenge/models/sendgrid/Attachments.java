package com.siteminder.challenge.models.sendgrid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Attachments {
    private String content;
    private String type;
    private String filename;
    private String disposition;
    @JsonProperty("content_id") private String contentId;
    
    public Attachments() {
        
    }
    
    public Attachments(String content, String type, String filename, String disposition, String contentId) {
        this.content = content;
        this.type = type;
        this.filename = filename;
        this.disposition = disposition;
        this.contentId = contentId;
    }
    
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getDisposition() {
        return disposition;
    }
    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }
    
    @JsonProperty("content_id")
    public String getContentId() {
        return contentId;
    }
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }   
}
