'use strict'

onslyde.Controllers.controller('LoginCtrl', [ '$store', '$http', '$scope', '$rootScope', '$routeParams', '$location', function ( $store, $http, $scope, $rootScope, $routeParams, $location) {

  $store.bind($rootScope,'userInfo');





  $scope.login = function(email,password) {

    var email = (email || $scope.login_email);
    var password = (password || $scope.login_password);

    $http({method: 'POST', url: $scope.urls() + '/go/members/login', data: $.param({email:email,password:password}), headers: {'Content-Type': 'application/x-www-form-urlencoded'}}).
      success(function(data, status, headers, config) {
        if(data.created){
          $('#signin').foundation('reveal', 'close');
          $rootScope.userInfo = data;
          $store.set('userInfo',$rootScope.userInfo)
          $store.bind($rootScope,'userInfo');
          if($rootScope.userInfo && !$rootScope.userInfo.created){
            $scope.loginAlert = 'There was a problem logging in.'
          }
        }
      }).
      error(function(data, status, headers, config) {
        $scope.loginAlert = 'Wrong password or user not found.'
      });


  }


  //hooks into foundations validation - todo - fix with oob angular (or directive)
  $('#signin').on('invalid', function () {}).on('valid', function () {
    $scope.$apply(function() {
      $scope.login();
    });

  });

  $('#sign-up').on('invalid', function () {}).on('valid', function () {
    $scope.$apply(function() {
      $scope.signup();
    });

    });

  $scope.register = {};
  $scope.registerMessage = '';

  $scope.signup = function() {

    var email = $scope.register.email;
    var password = $scope.register.password;
    var name = $scope.register.fullName;


    $http({method: 'POST', url: $scope.urls() + '/go/members', data: $scope.register, headers: {}}).
      success(function(data, status, headers, config) {
        $rootScope.registerMessage = data;
        $store.set('registerMessage',$rootScope.registerMessage)
        $scope.login(email,password)
        $location.path('/gettingstarted');
      }).
      error(function(data, status, headers, config) {
        $scope.registerAlert = 'Problem registering user.'
      });


  }

  $scope.logout = function() {
    $store.remove('userInfo')
    $store.remove('registerMessage')
    $rootScope.userInfo = {};
    $rootScope.registerMessage = {};
  }


  $rootScope.$on('userNotAuthenticated', function(event) {
    $location.path('/login');


  });



  $scope.$on('loginSuccesful', function(event, user, organizations, applications) {

  });


}]);