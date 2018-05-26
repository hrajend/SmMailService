package com.siteminder.challenge.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
 * Implementation of SmMailService (SiteMinder Mail Service) REST controller.
 */
@RestController
public class SmMailServiceController {
	@Autowired
	private SmMailService mailService;
	
    @RequestMapping(value = "/emails", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> sendEmails(
    					@RequestPart("email_params") String emailParams, 
    					 @RequestPart("attachments") List<MultipartFile> attachments) {
    	return mailService.sendEmail(emailParams, attachments);
    }
}
