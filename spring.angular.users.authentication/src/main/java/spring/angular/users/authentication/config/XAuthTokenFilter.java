package spring.angular.users.authentication.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import spring.angular.users.authentication.utiles.TokenUtils;

/**
 * This filter is used to validate and get UserDetails from client login info. 
 * All incoming requests from the client go through this 'doFilter'. 
 * It uses the token system  to protect the site from CSRF and hackers.  
 * See: https://www.acunetix.com/websitesecurity/csrf-attacks/
 * To learn more about CSRF Attacks and how to defend from them.
 * Note: to make the site really secure, and not just a study toy, 
 * you must also enable ssl in the 'application.properties' config file. 
 */
public class XAuthTokenFilter extends GenericFilterBean {
	//A key, that appears in the header of every message from the client to the server.
	//Its value is the security token, that is used to verified the user identity.
	protected static final String X_AUTH_TOKEN_HEADER_NAME  = "x-auth-token";
	protected final UserDetailsService m_detailsService;
    protected final TokenUtils m_tokenUtils;

    public XAuthTokenFilter(UserDetailsService userDetailsService1) {
        m_detailsService = userDetailsService1;
        m_tokenUtils = new TokenUtils();
    }

    //This filter is used to validate and get UserDetails from client login info.
    //All incoming requests (except login) from the client go through this 'doFilter'.
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			//Get token out of the client incoming message.
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String authToken = httpServletRequest.getHeader(X_AUTH_TOKEN_HEADER_NAME);
            //Get the user name from the token, get the user from the repository by its name, 
            //and validate the token using the user info. Return null for invalid token.
            UserDetails details = getUserFromToken(authToken, httpServletRequest); 
            if (details!=null){
            	//Set the current user. The method 'getCurrentUser' in 'UsersService' return it. 
                UsernamePasswordAuthenticationToken currentUser = new UsernamePasswordAuthenticationToken 
                		(details, details.getPassword(), details.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(currentUser);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
	}

	//Get the user name from the token, get the user from the repository by its name, 
    //and validate the token using the user info. Return null for invalid token.
	protected UserDetails getUserFromToken(String authToken, HttpServletRequest request) {
		if (!StringUtils.hasText(authToken)){
            return null;
        }
		String userName = m_tokenUtils.getUserNameFromToken(authToken);
		try{
			UserDetails details = m_detailsService.loadUserByUsername(userName); 
			if (!m_tokenUtils.validateToken(authToken, details)) {
				return null;
			}
			return details;
		}catch (Exception e){
			return null;
		}
	}
}