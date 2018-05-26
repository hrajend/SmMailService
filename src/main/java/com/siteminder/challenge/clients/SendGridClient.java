package com.siteminder.challenge.clients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siteminder.challenge.SmMailServiceConfig;
import com.siteminder.challenge.commons.Constants;
import com.siteminder.challenge.core.ApiResException;
import com.siteminder.challenge.models.Email;
import com.siteminder.challenge.models.EmailParams;
import com.siteminder.challenge.models.sendgrid.Attachments;
import com.siteminder.challenge.models.sendgrid.Content;
import com.siteminder.challenge.models.sendgrid.Personalization;
import com.siteminder.challenge.models.sendgrid.SgMail;

/*
 * Implementation for consuming SendGrid mailing service.
 * Extends from abstract mail client and it's own logic for sendMail abstract method.
 */
public class SendGridClient extends SmMailClient {
	public SendGridClient(EmailParams emailParams, List<MultipartFile> attachments, SmMailServiceConfig serviceConfig) {
		super(emailParams, attachments, serviceConfig);
	}
	
	/*
	 * Validates the email parameters.
	 * Constructs the model required to consume SendGrid mailing service.
	 * Consumes the API served SendGrid.
	 */	
	public void sendEmail() throws ApiResException {
		validateEmailParams();
		SgMail sgMail = new SgMail();
		constructSendGridModel(sgMail);		
		invokeSendApi(sgMail);
	}
	
	/*
	 * Constructs the model required to consume SendGrid mailing service.
	 */
	private void constructSendGridModel(/*out*/ SgMail sgMail) throws ApiResException {
		EmailParams params = getEmailParams();
		
		// Set from email
		sgMail.setFrom(params.getFrom());
		
		// Set the personalization object (to, cc and bcc recipients)		
		constructPersonalizations(sgMail, params);
		
		// Set mail subject and content
		constructMessage(sgMail, params);
		
		// Set the attachments
		List<MultipartFile> inputAttachments = getAttachments();
		if(inputAttachments != null && inputAttachments.size() > 0) {
			constructAttachments(sgMail, inputAttachments);
		}
	}
	
	/*
	 * Constructs from, to, cc and bcc email addresses
	 */	
	private void constructPersonalizations(/*out*/ SgMail sgMail, EmailParams params) {
		List<Personalization> personalizations = new ArrayList<>();
		List<Email> to = params.getTo();
		List<Email> cc = params.getCc();
		List<Email> bcc = params.getBcc();		
		Personalization personalization = new Personalization();
		personalization.setTo(to); // At this stage, to cannot be null or 0-sized. So, no need to check that.
		if(cc != null && cc.size() > 0) {
			personalization.setCc(cc);
		}
		if(bcc != null && bcc.size() > 0) {
			personalization.setBcc(bcc);
		}
		personalizations.add(personalization);
		sgMail.setPersonalizations(personalizations);
	}
	
	/*
	 * Validates and constructs subject and message (email body)
	 */	
	private void constructMessage(SgMail sgMail, EmailParams params) throws ApiResException {
		String subject = params.getSubject();
		String message = params.getMessage();
		
		addSize(subject.length() + message.length());
		
		sgMail.setSubject(subject);
		sgMail.setContent(constructContent(message));		
	}
	
	/*
	 * Constructs message (email body)
	 */
	private List<Content> constructContent(String message) {
		List<Content> contentList = new ArrayList<>();
		Content content = new Content();
		content.setType(MediaType.TEXT_PLAIN_VALUE); //TODO: Change when supporting different types of content
		content.setValue(message);
		contentList.add(content);
		return contentList;
	}
	
	/*
	 * Validates and constructs attachments  
	 */	
	private void constructAttachments(/*out*/ SgMail sgMail, List<MultipartFile> inputAttachments) throws ApiResException {
		List<Attachments> attachmentList = new ArrayList<>();
		for(MultipartFile file : inputAttachments) {
			try {
				Attachments attachment = new Attachments();
				byte[] raw = file.getBytes();
				if(raw == null || raw.length <= 0) {
					HttpStatus status = HttpStatus.BAD_REQUEST;
					String errMsg = Constants.ApiStatusMessages.INVALID_ATTACHMENT;
					throw new ApiResException(status, errMsg);
				}
				
				byte[] encoded = Base64.encodeBase64(raw);
				attachment.setType(file.getContentType());
				attachment.setContent(new String(encoded));
				attachment.setFilename(file.getOriginalFilename());
				attachmentList.add(attachment);
				addSize(encoded.length);
				
			} catch(IOException ex) {
				ex.printStackTrace();
				HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
				String errMsg = Constants.ApiStatusMessages.ATTACHMENT_FAILED + "  " + file.getOriginalFilename();				
				throw new ApiResException(status, errMsg);
			}
		}
		sgMail.setAttachments(attachmentList);
	}	
	
	/*
	 * Executes the HTTP API of SendGrid
	 */	
	private void invokeSendApi(SgMail sgMail) throws ApiResException {
		HttpPost request = buildSgClientRequest(sgMail);
		executeHttpRequest(request);
	}
	
	/*
	 * Constructs the HTTP Request for MailGun API
	 */	
	private HttpPost buildSgClientRequest(SgMail sgMail) throws ApiResException {
		HttpPost request = new HttpPost(getServiceConfig().getEndPoint());
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getServiceConfig().getApiKey());
		request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);		
		
		try {	
			String sgMailJson = new ObjectMapper().writeValueAsString(sgMail);
			StringEntity sgMailEntity = new StringEntity(sgMailJson);
			request.setEntity(sgMailEntity);
		} catch(JsonProcessingException jsonEx) {
			jsonEx.printStackTrace();
			throw new ApiResException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ApiStatusMessages.SERVICE_JSON_FAILURE);
		} catch(UnsupportedEncodingException ueEx) {
			ueEx.printStackTrace();
			throw new ApiResException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ApiStatusMessages.SERVICE_ENCODING_FAILURE);
		}
		
		return request;
	}	
}
