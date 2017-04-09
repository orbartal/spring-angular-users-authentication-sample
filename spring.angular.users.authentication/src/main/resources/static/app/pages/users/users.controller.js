(function () {
    'use strict';

    angular
        .module('app')
        .controller('UsersController', UsersController);
    
    UsersController.$inject = ['UsersService', 'LoginService'];
    
    function UsersController (UsersService, LoginService) {
        var vm = this;
        vm.users =[];
        vm.currentUser = null;
        vm.currentUser = UsersService.getCurrentUser();
        
        vm.logout = function (){
        	LoginService.clearCredentials();
        }
        
        vm.getAll = function() {
        	UsersService.getAll().then(onSuccess, onFailure);
        	
        	 function onSuccess(data) {           
        		 vm.users = data;
             }

             function onFailure(error) {
            	 alert (error);
             }
        }
       vm.getAll();
    }
})();