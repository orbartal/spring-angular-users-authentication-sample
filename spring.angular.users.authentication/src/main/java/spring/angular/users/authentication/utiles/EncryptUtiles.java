package spring.angular.users.authentication.utiles;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;

/**
 * This class is providing signature and encryption services for the token utility class. 
 * Thus It is not manage by spring and it is created explicitly using its constructor. 
 * 
 * @author Or Bartal
 */
public class EncryptUtiles {
	
    protected MessageDigest m_digest = null;
    
    public EncryptUtiles (){
    	try {
            m_digest = MessageDigest.getInstance("MD5");
         } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
         }
    }
    
    public String encrypt(String strPlain){
    	byte[] arrPlain = strPlain.getBytes();
   	 	byte[] arrEncrypt = m_digest.digest(arrPlain);
        String strEncrypt = new String(Hex.encode(arrEncrypt));
        return strEncrypt;
   }
}
