package com.siteminder.challenge.models.sendgrid;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.siteminder.challenge.models.Email;

@JsonInclude(Include.NON_NULL)
public class Personalization {
	private List<Email> to;
	private List<Email> cc;
	private List<Email> bcc;
	private String subject;
	
	public Personalization() {
		
	}
	
	public Personalization(List<Email> to, List<Email> cc, List<Email> bcc, String subject) {
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
	}	
	
	public List<Email> getTo() {
		return to;
	}
	public void setTo(List<Email> to) {
		this.to = to;
	}
	public List<Email> getCc() {
		return cc;
	}
	public void setCc(List<Email> cc) {
		this.cc = cc;
	}
	public List<Email> getBcc() {
		return bcc;
	}
	public void setBcc(List<Email> bcc) {
		this.bcc = bcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
