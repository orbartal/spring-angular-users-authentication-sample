
(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);
    
    LoginController.$inject = ['LoginService', '$scope'];
    
    function LoginController(LoginService, $scope){
    	var vm = this;
    	vm.text = "Login with either user1:user1 or user2:user2 for limited user and with admin:admin for administrator user.";
    	vm.error = undefined; //Error message
    	vm.login = login;
    	
    	function login () {
    		LoginService.authenticate($scope.username, $scope.password).then(onSuccess, onFailure);
    		
    		function onSuccess (user) {
    			vm.error = undefined; //No errors
    		}

    		function onFailure (error) {
    			vm.error = error; //Error from the server to display to the user.
    		}
    	}
    }
})();    