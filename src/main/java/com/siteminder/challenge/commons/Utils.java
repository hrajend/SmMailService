package com.siteminder.challenge.commons;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;

import com.siteminder.challenge.core.ApiResException;
import com.siteminder.challenge.models.ApiResponse;
import com.siteminder.challenge.models.Email;

/*
 * Implementation of all common utility methods
 */
public final class Utils {
	/*
	 * Constructs an API response from input status and message parameters.
	 */
	public static ResponseEntity<?> buildApiResponse(HttpStatus status, String message) {
		ApiResponse apiResponse = new ApiResponse(status.value(), message);
		return new ResponseEntity<>(apiResponse, status);
	}
	
	/*
	 * Validates the format of an email address
	 */
	public static boolean isValidEmailFormat(String email) {
		boolean res = false;
		
		if(email == null || email.isEmpty() ) {
			return res;
		}
		if(GenericValidator.isEmail(email)) {
			res = true;
		}
		return res;
	}
	
	/*
	 * Validates the recipient list.
	 * Throws exception on finding duplicate recipients and invalid email address formats.
	 */	
	public static void validateRecipientList(List<Email> listToCheck, String msgToReturn,
												/*out*/ List<String> activeRecipients) throws ApiResException {
		for(Email emailEntry : listToCheck) {
			String email = emailEntry.getEmail();
			if(!Utils.isValidEmailFormat(email)) {
				//Fail even when there's one invalid recipient
				throw new ApiResException(HttpStatus.BAD_REQUEST, msgToReturn + " " + email);
			}
			
			if(!activeRecipients.contains(email)) {
				activeRecipients.add(email);
			} else {
				throw new ApiResException(HttpStatus.BAD_REQUEST, 
											Constants.ApiStatusMessages.DUPLICATE_RECEPIENT + " " + email);
			}
			
			if(activeRecipients.size() > Constants.Limitations.MAX_RECIPIENT) {
				throw new ApiResException(HttpStatus.BAD_REQUEST, Constants.ApiStatusMessages.MAX_RECIPIENT);
			}
		}
	}
	
	/*
	 * Converts the Email object to MailGun email format
	 */
	public static String getMailGunMailFormat(Email emailObj) {
		String outString = null;
		String email = emailObj.getEmail();
		
		if(email != null && isValidEmailFormat(email)) {
			String name = emailObj.getName();
			if (name != null && !name.isEmpty()) {
				outString = name + " <" + email + ">";
			} else {
				outString = email;
			}
		}
		
		return outString;
	}
	
	/*
	 * Converts the list of Email objects to MailGun email format
	 */	
	public static String getMailGunMailFormat(List<Email> emailList) {
		String outString = null;
		
		if(emailList == null || emailList.isEmpty()) {
			return null;
		}
		
		List<String> mailGunMailList = new ArrayList<>();		
		for(Email mail : emailList) {
			String mailStr = getMailGunMailFormat(mail);
			if(mailStr != null && !mailStr.isEmpty()) {
				mailGunMailList.add(mailStr);
			}
		}
		
		outString = String.join(",", mailGunMailList);
		
		return outString;
	}	
}
