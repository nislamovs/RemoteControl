// 'use strict';
//
// var app = angular.module('dashboardFE', [
//     'ui.router',
//     'ngStorage',
//     'ngAnimate',
//     'ngCookies',
//     'ngResource',
//     'ngRoute',
//     'ngSanitize',
//     'ngTouch'
// ]);
//
// app.constant('urls', {
//     BASE: 'href="http://backend:8080/DashboardIO',
//     USER_SERVICE_API: 'http://backend:8080/DashboardIO/api/user/'
// });
//
// app.config(['$stateProvider', '$urlRouterProvider',
//     function($stateProvider, $urlRouterProvider) {
//
//         $stateProvider
//             .state('home', {
//                 url: '/',
//                 templateUrl: 'partials/list',
//                 controller:'UserController',
//                 controllerAs:'ctrl',
//                 resolve: {
//                     users: function ($q, UserService) {
//                         console.log('Load all users');
//                         var deferred = $q.defer();
//                         UserService.loadAllUsers().then(deferred.resolve, deferred.resolve);
//                         return deferred.promise;
//                     }
//                 }
//             });
//
//         $urlRouterProvider
//             .when('/', {
//                 templateUrl: 'views/main.html',
//                 controller: 'MainCtrl',
//                 controllerAs: 'main'
//             })
//             .when('/about', {
//                 templateUrl: 'views/about.html',
//                 controller: 'AboutCtrl',
//                 controllerAs: 'about'
//             })
//             .otherwise({
//                 redirectTo: '/'
//             });
//     }]);
