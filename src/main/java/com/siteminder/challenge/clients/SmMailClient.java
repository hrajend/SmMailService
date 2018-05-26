package com.siteminder.challenge.clients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.siteminder.challenge.SmMailServiceConfig;
import com.siteminder.challenge.commons.Constants;
import com.siteminder.challenge.commons.Utils;
import com.siteminder.challenge.core.ApiResException;
import com.siteminder.challenge.models.Email;
import com.siteminder.challenge.models.EmailParams;

/*
 * Base class for MailGunClient and SendGridClient classes.
 * Holds all the common members of mailing service clients.
 * Holds all the common helper methods of mailing service clients.
 */
public abstract class SmMailClient {
	private EmailParams emailParams;
	private SmMailServiceConfig serviceConfig;
	private List<MultipartFile> attachments;
	private long size;
	
	private static final Log log = LogFactory.getLog(SmMailClient.class);
	
	public SmMailClient(EmailParams emailParams, List<MultipartFile> attachments, SmMailServiceConfig serviceConfig) {
		this.emailParams = emailParams;
		this.serviceConfig = serviceConfig;
		this.attachments = attachments;
		this.size = 0;
	}
	
	public EmailParams getEmailParams() {
		return emailParams;
	}

	public void setEmailParams(EmailParams emailParams) {
		this.emailParams = emailParams;
	}

	public SmMailServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	public void setServiceConfig(SmMailServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}
	
	public List<MultipartFile> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<MultipartFile> attachments) {
		this.attachments = attachments;
	}	
	
	/*
	 * Verify whether the email recipients mentioned are valid and does
	 * not exceed the maximum allowed recipients.
	 */
	public void validateEmailParams() throws ApiResException {
		if(emailParams == null) {
			log.error(Constants.ApiStatusMessages.EMPTY_MAIL_PARAMS);
			throw new ApiResException(HttpStatus.BAD_REQUEST, Constants.ApiStatusMessages.EMPTY_MAIL_PARAMS);
		}
		
		validateEmails();
		validateMessage();
	}
	
	
	/*
	 * Executes the given HTTP request.
	 * Throws an exception with appropriate error details on failure.
	 */
	public void executeHttpRequest(HttpPost request) throws ApiResException {
		CloseableHttpResponse response = null;
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			response = httpclient.execute(request);
			validateHttpResponse(response);
		} catch(ClientProtocolException cpEx) {
			cpEx.printStackTrace();
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			String errMsg = Constants.ApiStatusMessages.SERVICE_PROTOCOL_FAILURE;
			throw new ApiResException(status, errMsg);
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			String errMsg = Constants.ApiStatusMessages.SERVICE_API_FAILURE;
			throw new ApiResException(status, errMsg);
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	/*
	 * Validates the HTTP response form the mailing service consumed and
	 * throws an exception with appropriate error details on non success response.
	 */
	public void validateHttpResponse(CloseableHttpResponse response) throws ApiResException {
		int returnCode = response.getStatusLine().getStatusCode();
		if(returnCode != HttpStatus.ACCEPTED.value() && returnCode != HttpStatus.OK.value()) {
			//Error return appropriate error message
			//TODO: Map the error messages and form a generic error message that should be common
			// for different mail services.
			if(returnCode == HttpStatus.UNAUTHORIZED.value() || returnCode == HttpStatus.FORBIDDEN.value()) {
				HttpStatus status = HttpStatus.valueOf(returnCode);
				String errMsg = Constants.ApiStatusMessages.SERVICE_AUTH_FAILURE; 
				log.error(errMsg);
				throw new ApiResException(status, errMsg);
			} else {
				HttpStatus status = HttpStatus.valueOf(returnCode);
				String errMsg = Constants.ApiStatusMessages.SERVICE_API_FAILURE; 
				log.error(errMsg);
				throw new ApiResException(status, errMsg);
			}
		}		
	}
	
	/*
	 * Must be used whenever subject, message or an attachment is added to the email.
	 * Throws an exception when the maximum mail size is reached.
	 */
	public void addSize(long size) throws ApiResException {
		this.size += size;
		
		if(this.size >= Constants.Limitations.MAX_MAIL_SIZE) {
			throw new ApiResException(HttpStatus.BAD_REQUEST, Constants.ApiStatusMessages.MAIL_SIZE_EXCEEDED);
		}
	}
	
	/*
	 * Abstract method. Each mailing service client must implement it's own logic.
	 */
	public abstract void sendEmail() throws ApiResException;	
	
	// Validates the email addresses
	private void validateEmails() throws ApiResException {
		Email from = emailParams.getFrom();
		
		if(from == null) {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			String errMsg = Constants.ApiStatusMessages.INVALID_FROM; 
			log.error(errMsg);
			throw new ApiResException(status, errMsg);
		}
		
		String fromMail = from.getEmail();
		if(!Utils.isValidEmailFormat(fromMail)) {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			String errMsg = Constants.ApiStatusMessages.INVALID_FROM_EMAIL; 
			log.error(errMsg);
			throw new ApiResException(status, errMsg);
		}
		
		validateRecipients();
	}
	
	// Validates the to, cc and bcc recipients	
	private void validateRecipients() throws ApiResException {
		List<Email> to = emailParams.getTo();
		List<Email> cc = emailParams.getCc();
		List<Email> bcc = emailParams.getBcc();
		
		if(to == null || to.isEmpty()) {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			String errMsg = Constants.ApiStatusMessages.INVALID_TO; 
			log.error(errMsg);
			throw new ApiResException(status, errMsg);			
		}
		
		List<String> recipients = new ArrayList<>();
		Utils.validateRecipientList(to, Constants.ApiStatusMessages.INVALID_TO_EMAIL, recipients);
		
		if(cc != null && cc.size() > 0) {
			Utils.validateRecipientList(cc, Constants.ApiStatusMessages.INVALID_CC_EMAIL, recipients);
		}
		
		if(bcc != null && bcc.size() > 0) {
			Utils.validateRecipientList(bcc, Constants.ApiStatusMessages.INVALID_BCC_EMAIL, recipients);
		}		
	}
	
	// Validates subject and message (email body)	
	private void validateMessage() throws ApiResException {
		String message = emailParams.getMessage();
		if(message == null || message.isEmpty()) {
			log.error(Constants.ApiStatusMessages.INVALID_MESSAGE);
			throw new ApiResException(HttpStatus.BAD_REQUEST, Constants.ApiStatusMessages.INVALID_MESSAGE);
		}
		
		String subject = emailParams.getSubject();
		if(subject == null || subject.isEmpty()) {
			log.error(Constants.ApiStatusMessages.INVALID_SUBJECT);
			throw new ApiResException(HttpStatus.BAD_REQUEST, Constants.ApiStatusMessages.INVALID_SUBJECT);
		}		
	}
}
