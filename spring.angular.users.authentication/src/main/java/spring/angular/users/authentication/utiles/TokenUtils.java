package spring.angular.users.authentication.utiles;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This class translate user data to token and vise versa, while validating the token and user match. 
 * It is a utility class. It is not manage by spring and it is created explicitly using its constructor. 
 * 
 * @author Or Bartal
 */
public class TokenUtils {

	@Value("${server-client-secret-key}")
	protected String strSecretKey; 
	//This utility does all the signature and encryption action for the 'TokenUtils' methods.
    EncryptUtiles s_encryptUtiles = new EncryptUtiles ();

    //Create a token from the user data and the current time.
    //All to make it harder for an impostor to fake a token and pretend to be another user. 
    public String userToToken (UserDetails user) {
        long expires = System.currentTimeMillis() + 1000L * 60 * 60;
        String signature = computeSignature(user, expires);
        String strToken = user.getUsername() + ":" + expires + ":" + signature;
        return strToken;
    }

    protected String computeSignature(UserDetails user, long expires) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(user.getUsername()).append(":");
        signatureBuilder.append(expires).append(":");
        signatureBuilder.append(user.getPassword()).append(":");
        signatureBuilder.append(strSecretKey);
        String strPlain = signatureBuilder.toString();
        return s_encryptUtiles.encrypt(strPlain);
    }

    //Every token contain the user name as its first parameter. 
    public String getUserNameFromToken(String authToken) {
        String[] parts = authToken.split(":");
        return parts[0];
    }

    //Validate that the token match the user details and that it didn't expire
    public boolean validateToken(String authToken, UserDetails user) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];
        String signatureToMatch = computeSignature(user, expires);
        return (expires >= System.currentTimeMillis()) && (signature.equals(signatureToMatch));
    }
}