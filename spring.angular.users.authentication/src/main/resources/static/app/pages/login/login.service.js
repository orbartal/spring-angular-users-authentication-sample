(function () {
    'use strict';

    angular
        .module('app')
        .factory('LoginService', LoginService);

    LoginService.$inject = ['$state', 'UsersService', '$http', '$cookies'];
    function LoginService($state, UsersService, $http, $cookies) {
    	var service = {};
    	service.isLogin = false;
    	service.xAuthTokenHeaderName = 'x-auth-token'; //same as the key in the server
        service.authenticate = authenticate;
        service.isAuthenticated = isAuthenticated;
        service.setIsAuthenticated = setIsAuthenticated;
        service.clearCredentials = clearCredentials;
        return service;

        //Login
        function authenticate(username, password) {
        	var url = "/users/loginWithUserNameAndPassword";
        	var obj = {};
        	var args = {user : username, pass : password};
        	var parameters = {params: {'user': username, 'pass': password}};
        	return $http.post(url, {}, parameters).then(onSuccess, onFailure);
        	
        	// private functions
            function onSuccess(result) {
            	if (result && result.data) {
					var encodedTocken = result.data; 
					//This is the only line of code that makes the client-server messaging secure.. 
					$http.defaults.headers.common[service.xAuthTokenHeaderName] = encodedTocken;
					//Remember the login for future enters
				 	$cookies.putObject('current_user_tocken',  encodedTocken);
				 	$cookies.putObject('current_user_data', username );
				 	service.setIsAuthenticated(true, username);
				 	postLogin ();
				 	return result;
				} else {
					service.setIsAuthenticated(false);
					$http.defaults.headers.common[service.xAuthTokenHeaderName] = 'Undefined';
					throw "No token from the server";
				}
            }

            function onFailure(error) {
            	service.setIsAuthenticated(false);
				$http.defaults.headers.common[service.xAuthTokenHeaderName] = 'Undefined';
				if (!error){
					throw 'Unknown error';
				}else if (!error.data){
					if (error.message){
						throw error.message;
					}else{
						throw 'Unknown error';
					}
				}
				if (error.data.message){
					throw error.data.message;
				}
				throw error.data;
            }
        }
        
        function postLogin () {
        	$state.go("users");
		}
        
        //Set and get the current login user
        function setIsAuthenticated(authnetic, user) {
	    	if (authnetic){
	    		UsersService.setCurrentUser(user);
	    	}
	    	service.isLogin = authnetic;
	    }
        
        function isAuthenticated() {
        	service.isLogin =  UsersService.hasCurrentUser();
	    	if (!service.isLogin){
	    		getUserFromCookies();
	    		service.isLogin =  UsersService.hasCurrentUser();
	    	}
	    	return service.isLogin;
	    	
	    function getUserFromCookies (){
	    		var jsonTocken = $cookies.get('current_user_tocken');
	    		var jsonUserData = $cookies.get('current_user_data');
	    		if (!jsonTocken || !jsonUserData){
	    			return;
	    		}
	    		var userData =  JSON.parse(jsonUserData);
	    		var tocken =  JSON.parse(jsonTocken);
	    		service.setIsAuthenticated(true, userData);
	    		$http.defaults.headers.common[service.xAuthTokenHeaderName] = tocken;
	    		postLogin ();
	    	}
	    }
        
        //Logout
        function clearCredentials() {
	    	UsersService.setCurrentUser (undefined);
	    	service.isLogin = false;
	    	$cookies.remove("current_user_tocken");
	    	$cookies.remove("current_user_data");
	    	$state.go("login");
	    }
    }
})();
