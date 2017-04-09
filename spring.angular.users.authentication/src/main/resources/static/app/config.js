(function () {
    'use strict';
    
     angular.module('app').
     	config(config).
     	run(['$rootScope', '$state', 'LoginService', '$http', run]); //
     
     
     function run($rootScope, $state, LoginService, $http){ //
    	 $rootScope.$state = $state;
    	 
    	 
	    // Try to get a valid user from memory or cookie. 
    	 //If it can't it go to login page
		var isAuthenticated = LoginService.isAuthenticated(); 
		if (!isAuthenticated) {
			$state.go("login");
		}
     }//End run

    //Config: A single page application. 
     //angular-ui-router manage the access to different sub pages in ui-view.
    function config($stateProvider, $urlRouterProvider) {
    	$urlRouterProvider.otherwise("/login");
    	$stateProvider
    	.state('login', 
        		{
		            url: "/login",
		            templateUrl: "/app/pages/login/login.html",
		            controller: "LoginController as vm"
        		}
        )
    	.state('users', {
            url: "/users",
            templateUrl: "/app/pages/users/users.html",
            controller: "UsersController as vm"
        })
    }

})();