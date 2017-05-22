package spring.angular.users.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import spring.angular.users.authentication.services.UsersService;

/**
 * Configuration class of the spring security: 
 * authentication and permission on files and resources.
 * 
 * @author Or Bartal
 */
@EnableWebMvcSecurity
@EnableWebSecurity(debug = false)
@Configuration
@Order
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired
    UsersService m_usersService;
    
    //You can set this parameter value in the configuration file: 'application.properties'.
    @Value("${is-site-secure}")
	protected String strSecurityType; 
    
    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception{
    	authManagerBuilder.userDetailsService(m_usersService);
    }
    
   //You can make the site open for everyone (without login) or to enforce users authentication (login).
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	if ("yes".equalsIgnoreCase(strSecurityType)){
    		configureDisableCsrf (http);
    	}else if ("no".equalsIgnoreCase(strSecurityType)){
    		configureUnsecure (http);
    	}else{
    		throw new Exception (
    				"The site 'application.properties' must have a value for 'is-site-secure'."
    				+ "It can be 'yes' or 'no' (without quotation marks).");
    	}
    }
    
    //Allow anonymous users to enter the entire site without login.
    protected void configureUnsecure (HttpSecurity http) throws Exception {
      http.csrf().disable();
  	  http.antMatcher("/**").authorizeRequests().anyRequest().permitAll();
    }

    //Enforce users authentication (login).
    protected void configureDisableCsrf(HttpSecurity http) throws Exception {
    	//Enable login (with password and user name) from any client app not just this server built in client.
        http.csrf().disable(); 
        //XAuthTokenConfigurer is used to validate and get UserDetails from client login info.
        SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> 
        	securityConfigurerAdapter = new XAuthTokenConfigurer(m_usersService);
        http.apply(securityConfigurerAdapter);
        
        //Config which pages are available for anonymous users and which require authentication.
        http.httpBasic().and().authorizeRequests()
         	.antMatchers(getPublicResources()).permitAll()
         	.anyRequest().authenticated()
         	.and().logout().logoutSuccessUrl("/");
    }

	private String[] getPublicResources() {
		String[] arrPublicResources = {"/",
	       		  "/index.html",
	       		  "/app/pages/login/**",
	       		  "/users/loginWithUserNameAndPassword",
	       		  "/**/*.js",  
	       		  "/**/*.css"};
		return arrPublicResources;
	}
}