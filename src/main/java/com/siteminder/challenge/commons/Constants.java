package com.siteminder.challenge.commons;

/*
 * Holds all the constants used in this project.
 */
public final class Constants {
	public final class MailService {
		public static final String SENDGRID = "sendgrid";
		public static final String MAILGUN = "mailgun";
	}
	
	public final class ApiStatusMessages {
		public static final String SEND_SUCCESS = "Successfully Queued.";
		public static final String MAIL_SERVICE_NOT_FOUND = "Mail services are not properly defined in app configuration.";
		public static final String MAIL_SERVICE_UNKNOWN = "Unknown mail service found in app configuration.";
		public static final String EMPTY_MAIL_PARAMS = "Email parameters are missing in form data. Please provide parameters json to consume this API.";
		public static final String INVALID_MAIL_PARAMS = "Email parameters are not in the expected format.";
		public static final String MAIL_PARAMS_PARSE_FAILURE = "Unexpected issue while parsing the email params.";
		public static final String INVALID_FROM = "From email not given.";
		public static final String INVALID_TO = "To recipients not given.";
		public static final String UNEXPECTED_MAIL_SERVICE = "Unexpected error in selecting the mail servce.";
		public static final String INVALID_FROM_EMAIL = "Invalid address specified in From field.";
		public static final String INVALID_TO_EMAIL = "Invalid address specified in To field.";
		public static final String INVALID_CC_EMAIL = "Invalid address specified in CC field.";
		public static final String INVALID_BCC_EMAIL = "Invalid address specified in BCC field.";
		public static final String DUPLICATE_RECEPIENT = "Duplicate recipients mentioned in to, cc or bcc list.";
		public static final String MAX_RECIPIENT = "Target recipients crossed the maximum limit.";
		public static final String ATTACHMENT_FAILED = "Unable to attach the file.";
		public static final String SERVICE_API_FAILURE = "Unable to invoke the send API of configured mail service.";
		public static final String INVALID_MESSAGE = "Invalid message. Message size should not be 0.";
		public static final String INVALID_SUBJECT = "Invalid subject. Subject size should not be 0.";
		public static final String INVALID_ATTACHMENT = "Invalid attachment. Attachment size should not be 0.";
		public static final String MAIL_SIZE_EXCEEDED = "Mail size exceeded. Mail size including subject, message and attachments should not exceed 20 MB.";
		
		public static final String SERVICE_JSON_FAILURE = "Failure in forming the input to configured mail service.";
		public static final String SERVICE_PROTOCOL_FAILURE = "Error in the HTTP protocol used to connect with configured mail service.";
		public static final String SERVICE_ENCODING_FAILURE = "Error in the encoding used in connection with configured mail service.";
		public static final String SERVICE_AUTH_FAILURE = "Authorization issue in connecting with configured mail service.";
	}
	
	public final class Limitations {
		public static final short MAX_RECIPIENT = 1000;
		public static final long MAX_MAIL_SIZE = 20 * 1024 * 1024; // 20 MB		
	}
	
	public final class MailGun {
		public static final String FROM = "from";
		public static final String TO = "to";
		public static final String CC = "cc";
		public static final String BCC = "bcc";
		public static final String ATTACHMENT = "attachment";
		public static final String SUBJECT = "subject";
		public static final String TEXT_MESSAGE = "text";		
		public static final String MAIL_SEPARATOR = ",";
	}
}
