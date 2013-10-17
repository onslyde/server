'use strict';

var onslyde = onslyde || {};

onslyde.Constants = angular.module('onslyde.constants', []);
onslyde.Services = angular.module('onslyde.services', []);
onslyde.Controllers = angular.module('onslyde.controllers', []);
onslyde.Filters = angular.module('onslyde.filters', []);
onslyde.Directives = angular.module('onslyde.directives', []);

angular.module('onslyde',
    ['ngResource',
      'ngRoute',
      'localStorage',
      'onslyde.filters',
      'onslyde.services',
      'onslyde.directives',
      'onslyde.constants',
      'onslyde.controllers']).config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {


    $routeProvider
      .when('/home', {templateUrl: 'home/home.html', controller: 'PageCtrl'})
      .when('/login', {templateUrl: 'global/login.html', controller: 'LoginCtrl'})
      .when('/gettingstarted', {templateUrl: 'gettingstarted/gettingstarted.html', controller: 'GetStartedCtrl'})
      .when('/register', {templateUrl: 'home/register.html', controller: 'PageCtrl'})
      .when('/analytics', {templateUrl: 'analytics/analytics.html', controller: 'AnalyticsCtrl'})
      .when('/analytics/edge2013', {templateUrl: 'analytics/edgeNY-2013.html', controller: 'AnalyticsCtrl'})
      .otherwise({redirectTo: '/home'});

    $locationProvider
      .html5Mode(false)
      .hashPrefix('!');


  }])
  .run(function ($rootScope) {
    $rootScope.$on('$viewContentLoaded', function () {

      angular.element(document).ready(function () {

        var tag = document.createElement('script');
        tag.src = "//www.youtube.com/iframe_api";
        var firstScriptTag = document.getElementsByTagName('script')[0];
        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);



        //very, very fugly
        window.runFoundation(window, document, undefined);
        window.runAbide(window, document, undefined);
        window.runOrbit(window, document, undefined);
        window.runReveal(window, document, undefined);
        window.runTooltip(window, document, undefined);


        $(document)
          .foundation();

        $("#how-can-use").on("orbit:after-slide-change", function (event, orbit) {
//          console.info("after slide change");
//          console.info("slide ", document.querySelectorAll('.slide'));
          var counter = 1;
          angular.forEach(document.querySelectorAll('.slide'), function (value, key) {

            if ((orbit.slide_number + 1) === counter) {
              value.classList.remove('hidden');
              value.classList.add('slide' + counter);
            } else {
              value.classList.add('hidden');
              value.classList.remove('slide' + counter);
            }
            counter++;
          });

        });

      });

//      $('#sign-up').on('invalid', function () {
//        alert('np')
//      })
//        .on('valid', function () {
//          alert('yes')
//        });
//      });

    });

  });

