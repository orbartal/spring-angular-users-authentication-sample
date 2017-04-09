package spring.angular.users.authentication.repositories;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import spring.angular.users.authentication.model.UserDbE;

/**
 * This class is the data layer.
 * For simplicity the data is constant and in memory. 
 * You can of course make it changeable or\and connect it to a real db.
 * See for example: spring-boot-angular-crud-sample at orbartal github.
 * 
 * @author Or Bartal
 */
@Repository
public class UserRepository {
	 //The roles determine the user permissions for different data and actions in the site.
	 public static final String ROLE_ADMIN = "ADMIN";
	 public static final String ROLE_USER = "USER";

	 //A constant list of users. In a real app this usually define in the sql db. 
	 protected List<UserDbE> m_lstUsers = Arrays.<UserDbE>asList(
			 		new UserDbE("user1", "user1", ROLE_USER),
	    			new UserDbE("user2", "user2", ROLE_USER),
	    			new UserDbE("admin", "admin", ROLE_ADMIN)
			 	);
	 
	 public UserDbE getByName(String username){
			for (UserDbE details : m_lstUsers){
	            if (details.getUsername().equalsIgnoreCase(username))
	                return details;
			}
			return null;
	 }
	 
	 public List<UserDbE> getAll () throws Exception {
		 return m_lstUsers;
	 } 
}