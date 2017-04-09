(function () {
    'use strict';

    angular
        .module('app')
        .factory('UsersService', UsersService);

    UsersService.$inject = ['$http'];
    function UsersService ($http) {
    	var service = {};
    	service.currentUserData = null;
        service.getAll = getAll;
        
        service.setCurrentUser = setCurrentUser;
        service.getCurrentUser = getCurrentUser;
        service.hasCurrentUser = hasCurrentUser;
        
        return service;

        function getAll() {
            return $http.get('/users/').then(onSuccess, onFailure);
        }
        
        //Set and get the current login user
        function setCurrentUser (user) {
        	service.currentUserData = user;      
        }
        
        function getCurrentUser () {
           return service.currentUserData;      
    	}
        
        function hasCurrentUser () {
        	var user = getCurrentUser();
            return (user!=null && user!=undefined) // Utils.isNotEmptyObject(user);      
     	}
        
        // private functions
        function onSuccess(result) {           
        	return result.data;
        }

        function onFailure(error) {
            throw error.data;
        }
    }

})();
