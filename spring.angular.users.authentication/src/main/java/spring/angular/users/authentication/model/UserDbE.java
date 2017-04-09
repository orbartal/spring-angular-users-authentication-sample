package spring.angular.users.authentication.model;

import java.util.HashSet;
import org.springframework.security.core.GrantedAuthority;

/**
 * My implementation of UserDetails. 
 * 
 * @author Or Bartal
 */
@SuppressWarnings("serial")
public class UserDbE extends org.springframework.security.core.userdetails.User {

	//There are two roles: Admin and User. You can of course add more roles. 
	protected String rol;
		
	public UserDbE(String username1, String password1,String rol1) {
		super (username1, password1, new HashSet<GrantedAuthority>()); 
		this.rol = rol1;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}
}
