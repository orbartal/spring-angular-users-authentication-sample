package spring.angular.users.authentication.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import spring.angular.users.authentication.model.UserDbE;
import spring.angular.users.authentication.repositories.UserRepository;
import spring.angular.users.authentication.utiles.TokenUtils;

/**
 * This class is the logic layer. 
 * It serve the controller and uses the repository to access the data layer. 
 * This layer also check the permissions of the current user for each action. 
 * It implements the UserDetailsService interface by implementing 'loadUserByUsername' method.
 * 
 * @author Or Bartal
 */
@Service ("UsersService")  // when DAO is spring bean
public class UsersService extends SpringBeanAutowiringSupport implements UserDetailsService {

	@Value("${is-site-secure}")
	protected String strSecurityType; 
	
	@Autowired
	protected UserRepository m_userRepository;
	TokenUtils m_tokenUtils = new TokenUtils();
	
	public String loginWithUserNameAndPassword(String username, String password) {
		UserDbE userDB =  getByNameAndPassword(username, password);
		String token = m_tokenUtils.userToToken(userDB);
		return token;
	}
	
	public UserDbE getByNameAndPassword (String username, String password) throws UsernameNotFoundException {
		UserDbE user = m_userRepository.getByName(username);
		if ((user == null) || (!user.getPassword().equals(password))) {
	            throw new UsernameNotFoundException("Incorrct username or password");
	    }
		return user;
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDbE userDB = m_userRepository.getByName(username);
        if (userDB == null) {
            throw new UsernameNotFoundException("Cannot find user by username " + username);
        }
        String authority = userDB.getRol();
        boolean userEnabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        org.springframework.security.core.userdetails.User  securityUser = 
        		new org.springframework.security.core.userdetails.User
        			(userDB.getUsername(), userDB.getPassword(), 
        			userEnabled, accountNonExpired, credentialsNonExpired, 
        			accountNonLocked, Collections.singleton(new SimpleGrantedAuthority(authority)));
        return securityUser;
	}

	public List<UserDbE> getAllEntities() throws Exception {
		if (("no".equalsIgnoreCase(strSecurityType))){
			 return m_userRepository.getAll();
		}
		UserDbE currentUser = getCurrentUser();
		if (currentUser.getRol()==UserRepository.ROLE_ADMIN){
			 return m_userRepository.getAll();
		}else{
			 return Arrays.asList(currentUser);
		}
	}
	//The current user is set for each client request by method 'doFilter' in class 'XAuthTokenFilter'.
	//The method 'doFilter' validate the authentication of the user, while all the other method
	//trust this method to get the current user, and make no other validation about its identity. 
	public UserDbE getCurrentUser() throws Exception {
		 UserDbE currentUser = null;
		 try	{
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				String name = auth.getName();  
			   	currentUser = m_userRepository.getByName(name);
			   	if (currentUser==null) {
					throw new Exception("There is no user with the name: " + name + ".");
				}
		  }	catch(Exception e) {
				throw new Exception("get current user faild", e);
		  }
		  return currentUser;
	 }
}