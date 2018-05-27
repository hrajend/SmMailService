package com.siteminder.challenge.clients;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.siteminder.challenge.SmMailServiceConfig;
import com.siteminder.challenge.commons.Constants;
import com.siteminder.challenge.commons.Utils;
import com.siteminder.challenge.core.ApiResException;
import com.siteminder.challenge.models.Email;
import com.siteminder.challenge.models.EmailParams;

/*
 * Implementation for consuming MailGun mailing service.
 * Extends from abstract mail client and it's own logic for sendMail abstract method.
 */
public class MailGunClient extends SmMailClient {
    public MailGunClient(EmailParams emailParams, List<MultipartFile> attachments, SmMailServiceConfig serviceConfig) {
        super(emailParams, attachments, serviceConfig);
    }
    
    /*
     * Validates the email parameters.
     * Constructs the model required to consume MailGun mailing service.
     * Consumes the API served MailGun.
     */
    public void sendEmail() throws ApiResException {
        validateEmailParams();  
        
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        constructMailGunEntity(builder);
        invokeSendApi(builder);
    }
    
    /*
     * Constructs the model required to consume MailGun mailing service.
     */
    private void constructMailGunEntity(/*out*/ MultipartEntityBuilder builder) throws ApiResException {
        EmailParams params = getEmailParams();
        List<MultipartFile> attachments = getAttachments();
        
        constructEmailAddresses(builder, params);
        constructMessage(builder, params);
        
        if(attachments != null && attachments.size() > 0) {
            constructAttachments(builder, attachments);
        }
    }
    
    /*
     * Constructs from, to, cc and bcc email addresses
     */
    private void constructEmailAddresses(/*out*/MultipartEntityBuilder builder, EmailParams params) {
        //Fill from field
        Email from = params.getFrom();
        builder.addTextBody(Constants.MailGun.FROM, Utils.getMailGunMailFormat(from));
        
        //Fill to, cc and bcc fields
        List<Email> to = params.getTo();
        List<Email> cc = params.getCc();
        List<Email> bcc = params.getBcc();
        String toListStr = Utils.getMailGunMailFormat(to);
        String ccListStr = Utils.getMailGunMailFormat(cc);
        String bccListStr = Utils.getMailGunMailFormat(bcc);
        
        builder.addTextBody(Constants.MailGun.TO, toListStr); // At this stage, to cannot be null or 0-sized. So, no need to check that.
        
        if(ccListStr != null && !ccListStr.isEmpty()) {
            builder.addTextBody(Constants.MailGun.CC, ccListStr);
        }
        
        if(bccListStr != null && !bccListStr.isEmpty()) {
            builder.addTextBody(Constants.MailGun.CC, bccListStr);
        }       
    }
    
    /*
     * Validates and constructs subject and message (email body)
     */
    private void constructMessage(/*out*/ MultipartEntityBuilder builder, 
                                            EmailParams params) throws ApiResException {
        String subject = params.getSubject();
        String message = params.getMessage();
        
        addSize(subject.length() + message.length());
        
        builder.addTextBody(Constants.MailGun.SUBJECT, subject);
        builder.addTextBody(Constants.MailGun.TEXT_MESSAGE, message);       
    }   
    
    /*
     * Validates and constructs attachments  
     */
    private void constructAttachments(/*out*/ MultipartEntityBuilder builder, 
                                List<MultipartFile> attachments) throws ApiResException {
        for(MultipartFile file : attachments) {
            try {
                byte[] raw = file.getBytes();
                if(raw == null || raw.length <= 0) {
                    HttpStatus status = HttpStatus.BAD_REQUEST;
                    String errMsg = Constants.ApiStatusMessages.INVALID_ATTACHMENT;
                    throw new ApiResException(status, errMsg);
                }
                
                /*Size calculation should be based on Base64, as the size limitation should be same
                 * when using MailGun as well as SendGrid.
                 * SendGrid API accepts attachments as base64 strings.
                 */
                addSize(Base64.encodeBase64(raw).length);
                
                ContentType contentType =  ContentType.create(file.getContentType());
                InputStream iStream = file.getInputStream();
                String fileName = file.getOriginalFilename();
                builder.addBinaryBody(Constants.MailGun.ATTACHMENT, iStream, contentType, fileName);
            } catch(IOException ioEx) {
                ioEx.printStackTrace();
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                String fileName = file.getOriginalFilename();
                String errMsg = Constants.ApiStatusMessages.ATTACHMENT_FAILED + "  " + fileName;                
                throw new ApiResException(status, errMsg);
            }
        }
    }
    
    /*
     * Executes the HTTP API of MailGun
     */
    private void invokeSendApi(MultipartEntityBuilder builder) throws ApiResException {
        HttpPost request = buildMgClientRequest(builder);
        executeHttpRequest(request);
    }
    
    /*
     * Constructs the HTTP Request for MailGun API
     */
    private HttpPost buildMgClientRequest(MultipartEntityBuilder builder) {
        HttpPost request = new HttpPost(getServiceConfig().getEndPoint());

        // Set the authentication header 
        String auth = getServiceConfig().getApiKey();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        
        // Set the form data
        HttpEntity entity = builder.build();
        request.setEntity(entity);
        return request;
    }
}
