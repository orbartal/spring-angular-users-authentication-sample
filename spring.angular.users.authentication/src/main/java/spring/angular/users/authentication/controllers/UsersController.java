package spring.angular.users.authentication.controllers;

import java.util.List;

import javax.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.angular.users.authentication.model.UserDbE;
import spring.angular.users.authentication.services.UsersService;

/**
 * The controller is the api from the server to the client.
 * For simplicity this site contain a single controller. 
 * You can of course add more methods and\or more controllers.
 * 
 * @author Or Bartal
 */
@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
    UsersService m_usersService;
	
	//This method enable users to login, with name and password, to the site. Return there unique token.
	//To avoid string-plain-text and object-as-json-text confusion this method return ResponseEntity<String>.
	@RequestMapping(value = "/loginWithUserNameAndPassword", method = RequestMethod.POST,  produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> loginWithUserNameAndPassword(
			@RequestParam("user") String username, @RequestParam("pass") String password,
			ServletRequest request) throws Exception {
		try{
			String token = m_usersService.loginWithUserNameAndPassword (username, password);
			return ResponseEntity.status(HttpStatus.OK).body(token);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
		}
	}
	
	//Get all the users that this user is allowed to see: all by admin but only his for the rest.
	@RequestMapping(method = RequestMethod.GET)
	public List<UserDbE> getAllUsers() throws Exception {
		 List<UserDbE> lstUsers =  m_usersService.getAllEntities();
		 return lstUsers;	
	}
}
