package com.siteminder.challenge.core;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.siteminder.challenge.models.EmailParams;
import com.siteminder.challenge.SmAppConfig;
import com.siteminder.challenge.SmMailServiceConfig;
import com.siteminder.challenge.clients.MailGunClient;
import com.siteminder.challenge.clients.SendGridClient;
import com.siteminder.challenge.commons.Constants;
import com.siteminder.challenge.commons.Utils;

/*
 * Implementation of SmMailService (SiteMinder Mail Service)
 * Exposes a REST API to send email using SendGrid or MailGun mail service.
 */
@Component
public class SmMailService {
    private static final Log log = LogFactory.getLog(SmMailService.class);
    
    @Autowired
    SmAppConfig appConfig;
    
    /*
     * Validates and constructs email parameters.
     * Sends the email to recipients mentiones in email parameters.
     */
    public ResponseEntity<?> sendEmail(String emailParams, List<MultipartFile> attachments) {
        // 1. Error if the email parameters are not specified.
        if(emailParams == null || emailParams.isEmpty()) {
            log.error("Email parameters are not valid. Returning appropriate error.");
            return Utils.buildApiResponse(HttpStatus.BAD_REQUEST, Constants.ApiStatusMessages.EMPTY_MAIL_PARAMS);       
        }
        
        // 2. Construct EmailParams from JSON string. Return appropriate error message on failure.
        // 3. If sendMail fails, convert the ApiResException and return appropriate error response.
        try {
            EmailParams params = constructEmailParams(emailParams);
            sendEmail(params, attachments);
        } catch(ApiResException apiEx) {
            return Utils.buildApiResponse(apiEx.getStatus(), apiEx.getMessage());
        }
        
        // 4. If sendMail succeeds, send successful response        
        return Utils.buildApiResponse(HttpStatus.ACCEPTED, Constants.ApiStatusMessages.SEND_SUCCESS);
    }
    
    /*
     * Validates the input string and constructs EmailParams object. 
     */
    private EmailParams constructEmailParams(String emailParams) throws ApiResException {
        EmailParams params = null;
        
        try {
            params = new ObjectMapper().readValue(emailParams, EmailParams.class);
        } catch (JsonParseException | JsonMappingException jsonEx) {
            log.error("Error in parsing email params JSON. Returning appropriate error.");
            jsonEx.printStackTrace();
            throw new ApiResException(HttpStatus.BAD_REQUEST, Constants.ApiStatusMessages.INVALID_MAIL_PARAMS);         
        } catch (IOException ex) {
            ex.printStackTrace();           
            throw new ApiResException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ApiStatusMessages.MAIL_PARAMS_PARSE_FAILURE); 
        }
        
        return params;
    }
    
    /*
     * Parses through the available mail services in the defined priority order
     * and executes the send APIs of those services until at-least one invocation gets successful.  
     */ 
    private void sendEmail(EmailParams params, List<MultipartFile> attachments) throws ApiResException {
        List<SmMailServiceConfig> mailServiceConfigs = appConfig.getSmMailServiceConfig();
        ApiResException ex = null;
        
        for(SmMailServiceConfig config : mailServiceConfigs) {
            try {
                sendEmailUsingService(config, params, attachments);
                ex = null; // Reset the ex thrown in the previous iterartion. (Makes sense only after the 1st iteration)
            } catch(ApiResException apiEx) {
                ex = apiEx;
            }

            // Don't try sending mail with other mail services, if the failure is due to BadRequest.
            if(ex != null) {
                HttpStatus respStatus = ex.getStatus();
                if(HttpStatus.BAD_REQUEST.equals(respStatus)) {
                    log.error("Mail service API invocation failed.");
                    throw ex;
                }
            } else {
                // No exception means successfull queue of mailing request. So return.
                return;
            }
        }
        
        // Tried all mailing services. Not able to send e-mails using any of the mailing services.
        // Return the error details returned by the last tried mailing service.
        if(ex != null) {
            throw ex;
        }
    }
    
    /*
     * Sends the email using the input mailing service config.
     */
    private void sendEmailUsingService(SmMailServiceConfig config, EmailParams params, List<MultipartFile> attachments)
                        throws ApiResException {
        if(Constants.MailService.SENDGRID.equalsIgnoreCase(config.getServiceName())) {
            log.info("Consuming SendGrid mail service...");
            SendGridClient sgClient = new SendGridClient(params, attachments, config);
            sgClient.sendEmail();
        } else if(Constants.MailService.MAILGUN.equalsIgnoreCase(config.getServiceName())) {
            log.info("Consuming MailGun mail service...");
            MailGunClient mgClient = new MailGunClient(params, attachments, config);
            mgClient.sendEmail();
        } else {
            // It should never come here.
            log.error("It should not come here. Some breaking change in code. Returning a generic error.");
            throw new ApiResException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ApiStatusMessages.UNEXPECTED_MAIL_SERVICE);
        }       
    }
}
