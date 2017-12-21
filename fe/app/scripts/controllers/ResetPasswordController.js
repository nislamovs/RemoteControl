(function () {
    'use strict';

    angular
        .module('dashboardFE')
        .controller('ResetPasswordController', ResetpassController);

    ResetpassController.$inject = ['AuthenticationService', 'FlashService', '$location'];
    function ResetpassController(AuthenticationService, FlashService, $location) {
        var vm = this;

        vm.resetPass = resetPass;

        function resetPass() {
            vm.dataLoading = true;
            AuthenticationService.resetpass(vm.user)
                .then(function () {
                        // if (response.success) {
                        FlashService.Success('Your password is being reset. Please check Your mail.', true);
                        $location.path('/login');
                        // } else {
                    }, function(){
                        // FlashService.Error(response.message);
                        FlashService.Error("Password was not reset! Use the old one, dude :)");
                        vm.dataLoading = false;
                    }
                );
        }
    }

})();
