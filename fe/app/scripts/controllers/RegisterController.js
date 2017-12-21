(function () {
    'use strict';

    angular
        .module('dashboardFE')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['AuthenticationService', '$location', '$rootScope', 'FlashService'];
    function RegisterController(AuthenticationService, $location, $rootScope, FlashService) {
        var vm = this;

        vm.register = register;

        function register() {
            vm.dataLoading = true;
            AuthenticationService.Create(vm.user)
                .then(function (response) {
                        FlashService.Success(response, true);
                        $location.path('/login');
                    }, function(response){
                        FlashService.Error(response);
                        vm.dataLoading = false;
                    }
                );
        }
    }

})();
