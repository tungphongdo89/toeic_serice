package com.migi.toeic.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.regex.Pattern;

/**
 *
 * @author KhietDT
 */
@Slf4j
public class PasswordUtil {
//    protected static final Logger logger = Logger.getLogger(PasswordUtil.class);

    /**
     * (		# Start of group
     * (?=.*\d)		#   must contains one digit from 0-9
     * (?=.*[a-z])	#   must contains one lowercase characters
     * (?=.*[A-Z])	#   must contains one uppercase characters
     * (?=.*[!@#$%^&*])	#   must contains one special symbols in the list "!@#$%^&*"
     * .		#   match anything with previous condition checking
     * {8,30}           #   length at least 8 characters and maximum of 30
     * )		# End of group
     */
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,30})";
    
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    /**
     * 
     * @param stringLength
     * @param option: 1: digits only; 2: digits + a..z; 3: digits + a..z + A..Z
     * @return
     */
    public static String generateRandomString(int stringLength, int option) {
        
        // Store character for generate string
        String strAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String strCapitalAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String strDigits = "0123456789";
        String specialCharacters = "!@#$%^&*";
        // Store string to generate
        String strValid;
        
        switch (option) {
	        case 1:
	            strValid = strDigits;
	            break;
	        case 2:
	            strValid = strAlphabet + strDigits +specialCharacters;
	            break;
	        case 3:
	            strValid = strAlphabet + strCapitalAlphabet + strDigits + specialCharacters;
	            break;
	        default:
	            strValid = strAlphabet + strCapitalAlphabet + strDigits +  specialCharacters;
	            break;
        }
        
        String stringRamdom = "";
        
        Random random = new Random();
        
        for (int i = 0; i < stringLength; i++) {
            int randnum = random.nextInt(strValid.length()); 
            stringRamdom = stringRamdom + strValid.charAt(randnum);
        }
        
        return stringRamdom;
    }


}
