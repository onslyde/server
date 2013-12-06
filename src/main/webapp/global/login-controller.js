'use strict'

onslyde.Controllers.controller('LoginCtrl', [ '$store', '$http', '$scope', '$rootScope', '$routeParams', '$location', function ( $store, $http, $scope, $rootScope, $routeParams, $location) {

  $store.bind($rootScope,'userInfo');


  $scope.loginButton = {label:"Sign In",disabled:false};

  $scope.login = function(email,password) {

//    if($scope.signin.$valid){

    $scope.loginButton.label = "Signing in...";
    $scope.loginButton.disabled = true;

    var email = (email || $scope.login.email);
    var password = (password || $scope.login.password);

    $http({method: 'POST', url: $rootScope.urls() + '/go/members/login', data: $.param({email:email,password:password}), headers: {'Content-Type': 'application/x-www-form-urlencoded'}}).
      success(function(data, status, headers, config) {
        if(data.created){
          $('#signin').foundation('reveal', 'close');
          $rootScope.userInfo = data;

          $store.set('userInfo',$rootScope.userInfo);
          $store.bind($rootScope,'userInfo');

        }else{
          $scope.loginAlert = 'There was a problem logging in: ' + data.name;
        }
        $scope.loginButton.label = "Sign In";
        $scope.loginButton.disabled = false;

      }).
      error(function(data, status, headers, config) {
        $scope.loginAlert = 'Wrong password or user not found.'
        $scope.loginButton.label = "Sign In";
        $scope.loginButton.disabled = false;
      });

//    }
//    $scope.signin.$setPristine();
  };


  $scope.register = {};
  $scope.registerMessage = '';
  $scope.signupButton = {label:"Create",disabled:false};

  $scope.registerUser = function() {

    if($scope.signup.$valid){

    $scope.signupButton.disabled = true;
    $scope.signupButton.label = "Creating...";

    var email = $scope.register.email;
    var password = $scope.register.password;
    var name = $scope.register.fullName;


    $http({method: 'POST', url: $rootScope.urls() + '/go/members', data: $scope.register, headers: {}}).
      success(function(data, status, headers, config) {
        $rootScope.registerMessage = data;
        $store.set('registerMessage',$rootScope.registerMessage)
        $scope.login(email,password)
        $location.path('/gettingstarted');
        $scope.signupButton.disabled = false;
        $scope.signupButton.label = "Create";
      }).
      error(function(data, status, headers, config) {
        $scope.registerAlert = 'Problem registering user: ' + data.email;
        $scope.signupButton.disabled = false;
        $scope.signupButton.label = "Create";
      });

    }


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