package com.siteminder.challenge.models.sendgrid;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.siteminder.challenge.models.Email;

@JsonInclude(Include.NON_NULL)
public class SgMail {
	private Email from;
	private List<Personalization> personalizations;
	private String subject;
	private List<Attachments> attachments;
	private List<Content> content;
	
	public SgMail() {
		
	}

	public SgMail(Email from, List<Personalization> personalizations, String subject, List<Attachments> attachments,
			List<Content> content) {
		this.from = from;
		this.personalizations = personalizations;
		this.subject = subject;
		this.attachments = attachments;
		this.content = content;
	}	
	
	public Email getFrom() {
		return from;
	}
	public void setFrom(Email from) {
		this.from = from;
	}
	public List<Personalization> getPersonalizations() {
		return personalizations;
	}
	public void setPersonalizations(List<Personalization> personalizations) {
		this.personalizations = personalizations;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public List<Attachments> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachments> attachments) {
		this.attachments = attachments;
	}
	public List<Content> getContent() {
		return content;
	}
	public void setContent(List<Content> content) {
		this.content = content;
	}	
}
