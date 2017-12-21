(function () {
    'use strict';

    angular
        .module('dashboardFE')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$http', 'UserService', '$rootScope', 'FlashService'];
    function HomeController($http, UserService, $rootScope, FlashService) {
        var vm = this;
        var changePassFormHidden=true;
        var prefix="http://localhost:8080/DashboardIO/auth";

        vm.username=$rootScope.globals.currentUser.username;
        vm.user = null;
        vm.allUsers = [];
        vm.deleteUser = deleteUser;
        vm.deleteAccount = deleteAccount;
        vm.changePass = changePass;

        initController();

        function initController() {
            loadFunctionality();
            loadAllUsers();
        }

        function deleteAccount() {
            $("#account_delete_dialog").text("Are you sure You want to delete your account?").dialog({
                title: "Confirmation",
                resizable: false,
                modal: true,
                buttons: {
                    "Yes" : function () {
                        $(this).dialog("close");
                        $http.delete(prefix+'/deleteuser/?username=' + $rootScope.globals.currentUser.username)
                            .then(function success(response) {
                                FlashService.Success('Account deleted successfully', true)
                            }, function error(response) {
                                FlashService.Error('Account deletion error.', true)
                            });
                        location.href = '#!/login';
                    },
                    "No" : function (){
                        $(this).dialog("close");
                    }
                }
            });
        }

        function changePass() {

            var username = {};
            username.email=vm.username;
            if(vm.user.password1 != vm.user.password2) {
                FlashService.Error('Password change error. Check input data.', true);
                return;
            }

            username.password=vm.user.password1;

            $http.put(prefix+'/changepass/', username)
                .then(function success(response) {
                    FlashService.Success('Password changed successfully', true);
                    location.href = '#!/login';
                }, function error(response) {
                    FlashService.Error('Password change error.', true);
                });

        }

        function loadFunctionality() {
            $("#hide_show").click(function () {
                if(changePassFormHidden==false) {
                    $("#change_pass_form").hide(200);
                    changePassFormHidden=true;
                } else {
                    $("#change_pass_form").show(200);
                    changePassFormHidden=false;
                }
            });

            $("input[type=password]").change(function(){
                var ucase = new RegExp("[A-Z]+");
                var lcase = new RegExp("[a-z]+");
                var num = new RegExp("[0-9]+");

                if($("#password1").val().length >= 8){
                    $("#8char").removeClass("glyphicon-remove");
                    $("#8char").addClass("glyphicon-ok");
                    $("#8char").css("color","#00A41E");
                }else{
                    $("#8char").removeClass("glyphicon-ok");
                    $("#8char").addClass("glyphicon-remove");
                    $("#8char").css("color","#FF0004");
                }

                if(ucase.test($("#password1").val())){
                    $("#ucase").removeClass("glyphicon-remove");
                    $("#ucase").addClass("glyphicon-ok");
                    $("#ucase").css("color","#00A41E");
                }else{
                    $("#ucase").removeClass("glyphicon-ok");
                    $("#ucase").addClass("glyphicon-remove");
                    $("#ucase").css("color","#FF0004");
                }

                if(lcase.test($("#password1").val())){
                    $("#lcase").removeClass("glyphicon-remove");
                    $("#lcase").addClass("glyphicon-ok");
                    $("#lcase").css("color","#00A41E");
                }else{
                    $("#lcase").removeClass("glyphicon-ok");
                    $("#lcase").addClass("glyphicon-remove");
                    $("#lcase").css("color","#FF0004");
                }

                if(num.test($("#password1").val())){
                    $("#num").removeClass("glyphicon-remove");
                    $("#num").addClass("glyphicon-ok");
                    $("#num").css("color","#00A41E");
                }else{
                    $("#num").removeClass("glyphicon-ok");
                    $("#num").addClass("glyphicon-remove");
                    $("#num").css("color","#FF0004");
                }

                if($("#password1").val() == $("#password2").val()){
                    $("#pwmatch").removeClass("glyphicon-remove");
                    $("#pwmatch").addClass("glyphicon-ok");
                    $("#pwmatch").css("color","#00A41E");
                }else{
                    $("#pwmatch").removeClass("glyphicon-ok");
                    $("#pwmatch").addClass("glyphicon-remove");
                    $("#pwmatch").css("color","#FF0004");
                }
            });
        }

        function loadCurrentUser() {
            UserService.GetByUsername($rootScope.globals.currentUser.username)
                .then(function (user) {
                    vm.user = user;
                });
        }

        function loadAllUsers() {
            UserService.GetAll()
                .then(function (users) {
                    vm.allUsers = users;
                    console.log(vm.allUsers);
                });
        }

        function deleteUser(id) {
            UserService.Delete(id)
                .then(function () {
                    loadAllUsers();
                });
        }
    }

})();