(function () {
    'use strict';

    angular.module('dashboardFE',['ngRoute', 'ngCookies'])
           .config(config)
           .constant('urls', {
                BASE: 'href="http://backend:8080/DashboardIO',
                USER_SERVICE_API: 'http://backend:8080/DashboardIO/api/user/'
            })
           .run(run);

    config.$inject = ['$routeProvider', '$locationProvider'];

    function config($routeProvider) {

        $routeProvider
            .when('/', {
                controller: 'HomeController',
                templateUrl: 'views/home.html',
                controllerAs: 'vm'
            })

            .when('/login', {
                controller: 'LoginController',
                templateUrl: 'views/login.html',
                controllerAs: 'vm'
            })

            .when('/resetpass', {
                controller: 'ResetPasswordController',
                templateUrl: 'views/resetpass.html',
                controllerAs: 'vm'
            })

            .when('/register', {
                controller: 'RegisterController',
                templateUrl: 'views/register.html',
                controllerAs: 'vm'
            })

            .otherwise({ redirectTo: '/login' });

        // $stateProvider
        //     .state('home', {
        //         url: '/',
        //         templateUrl: '../views/list.html',
        //         controller:'UserController',
        //         controllerAs:'ctrl',
        //         resolve: {
        //             users: function ($q, UserService) {
        //                 console.log('Load all users');
        //                 var deferred = $q.defer();
        //                 UserService.loadAllUsers().then(deferred.resolve, deferred.resolve);
        //                 return deferred.promise;
        //             }
        //         }
        //     });
        // $urlRouterProvider.otherwise('/');
    }

    run.$inject = ['$rootScope', '$location', '$cookies', '$http'];
    function run($rootScope, $location, $cookies, $http) {
        // keep user logged in after page refresh
        $rootScope.globals = $cookies.getObject('globals') || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            // redirect to login page if not logged in and trying to access a restricted page
            var restrictedPage = $.inArray($location.path(), ['/login', '/register', '/resetpass']) === -1;
            var loggedIn = $rootScope.globals.currentUser;
            if (restrictedPage && !loggedIn) {
                $location.path('/login');
            }
        });
    }

})();