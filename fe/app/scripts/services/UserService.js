(function () {
    'use strict';

    angular
        .module('dashboardFE')
        .factory('UserService', UserService);

    UserService.$inject = ['$http', '$q'];
    function UserService($http, $q) {

        var prefix="http://localhost:8080/DashboardIO/api";
        var service = {};

        service.GetAll = GetAll;
        service.GetById = GetById;
        service.GetByUsername = GetByUsername;
        service.Update = Update;
        service.Delete = Delete;

        return service;

        function GetAll() {
            return $http.get(prefix+'/user/').then(handleSuccess, handleError('Error getting all users'));
        }

        function GetById(id) {
            return $http.get(prefix+'/user/' + id).then(handleSuccess, handleError('Error getting user by id'));
        }

        function GetByUsername(username) {
            return $http.get(prefix+'/user/' + username).then(handleSuccess, handleError('Error getting user by username'));
        }

        function Update(user) {
            return $http.put(prefix+'/user/' + user.id, user).then(handleSuccess, handleError('Error updating user'));
        }

        function Delete(id) {
            return $http.delete(prefix+'/user/' + id).then(handleSuccess, handleError('Error deleting user'));
        }

        // private functions

        function handleSuccess(res) {
            return res.data;
        }

        function handleError(error) {
            return function () {
                return { success: false, message: error };
            };
        }
    }

})();
