package com.example.libraryisbn;

import java.util.regex.Pattern;

import android.text.Html;
import android.widget.EditText;
 
public class Validation {
 
    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{10}";
    private static final String REGNO_REGEX = "\\d{10}";
 
    // Error Messages
    private static final String REQUIRED_MSG = " required";
    private static final String INVALID_MSG = " invalid";
    
    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, INVALID_MSG, required);
    }
 
    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, INVALID_MSG, required);
    }
    
    public static boolean isRegistrationNumber(EditText editText, boolean required) {
        return isValid(editText, REGNO_REGEX, INVALID_MSG, required);
    }
 
    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {
        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);
 
        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;
 
        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(Html.fromHtml("<font color='red'>" + editText.getHint()+errMsg + "</font>"));
            return false;
        };
 
        return true;
    }
 
    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {
 
        String text = editText.getText().toString().trim();
        editText.setError(null);
 
        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(Html.fromHtml("<font color='red'>" + editText.getHint()+REQUIRED_MSG + "</font>"));
            return false;
        }
 
        return true;
    }
}
