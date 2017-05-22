package spring.angular.users.authentication.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
 * 
 * Learn more about CSRF Attacks and how to defend from them:
 * https://www.acunetix.com/websitesecurity/csrf-attacks/
 * https://www.owasp.org/index.php/Cross-Site_Request_Forgery 
 * https://en.wikipedia.org/wiki/Cross-site_request_forgery 
 * 
 * Note: to make the site really secure, and not just a study toy, 
 * you must also enable SSL in the 'application.properties' config file. 
 */
public class XAuthTokenFilter extends GenericFilterBean {
	//A key, that appears in the header of every message from the client to the server.
	//Its value is the security token, that is used to verified the user identity.
	protected static final String X_AUTH_TOKEN_HEADER_NAME  = "x-auth-token";
	protected final UserDetailsService m_usersDetailsService;
    protected final TokenUtils m_tokenUtils;
    protected Set <String> m_allowdMethods = null;

    public XAuthTokenFilter(UserDetailsService userDetailsService1) {
        m_usersDetailsService = userDetailsService1;
        m_tokenUtils = new TokenUtils();
        m_allowdMethods = new HashSet <String> (Arrays.asList("GET", "PUT", "POST", "DELETE"));
    }

    //This filter is used to validate and get UserDetails from client login info.
    //All incoming requests (except login) from the client go through this 'doFilter'.
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			//Get token out of the client incoming message.
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            if (!isValidRequest (httpServletRequest)){
            	throw new ServletException (httpServletRequest.toString());
            }
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
            throw new ServletException(ex);
        }
	}
	
	//Enforce sameOriginPolicy, as a protection method against csrf attacks.
	//Can also support cors. http://caniuse.com/#search=cors and https://www.html5rocks.com/en/tutorials/cors/
	//Note origin is null if the request is same origin. Otherwise the browser add origin and host headers.
	//See https://stackoverflow.com/questions/15512331/chrome-adding-origin-header-to-same-origin-request
	protected boolean isValidRequest (HttpServletRequest httpServletRequest){
		String clientOrigin = httpServletRequest.getHeader("origin");
		String clientHost = httpServletRequest.getHeader("host");
		String method = httpServletRequest.getMethod();
		if (!m_allowdMethods.contains(method)){
			return false;
		}
		if (httpServletRequest.getUserPrincipal()==null){
			return true;
		}else if (clientOrigin==null || clientOrigin.equalsIgnoreCase(clientHost)){
			return true;
		}
		return isValidCorsRequest(httpServletRequest, clientOrigin);
	}

	private boolean isValidCorsRequest(HttpServletRequest httpServletRequest, String clientOrigin) {
		//Do not support cors by default. But can easily add support here.
		return false;
	}

	//Get the user name from the token, get the user from the repository by its name, 
    //and validate the token using the user info. Return null for invalid token.
	protected UserDetails getUserFromToken(String authToken, HttpServletRequest request) {
		if (!StringUtils.hasText(authToken)){
            return null;
        }
		String userName = m_tokenUtils.getUserNameFromToken(authToken);
		try{
			UserDetails details = m_usersDetailsService.loadUserByUsername(userName); 
			if (!m_tokenUtils.validateToken(authToken, details)) {
				return null;
			}
			return details;
		}catch (Exception e){
			return null;
		}
	}
}