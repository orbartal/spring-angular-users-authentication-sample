package spring.angular.users.authentication.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * A link class between WebSecurityConfig (the main configuration class of the spring security)
 * And XAuthTokenFilter (the class that get the server UserDetails from the client login info).
 * 
 * @author Or Bartal
 */
public class XAuthTokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	protected UserDetailsService m_detailsService;

	public XAuthTokenConfigurer(UserDetailsService detailsService) {
		this.m_detailsService = detailsService;
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		XAuthTokenFilter customFilter = new XAuthTokenFilter(m_detailsService);
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
