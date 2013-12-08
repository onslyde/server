'use strict'

onslyde.Controllers.controller('PageCtrl',
  [ 'pagedata',
    'utility',
    '$scope',
    '$rootScope',
    '$location',
    '$routeParams',
    '$q',
    '$route',
    '$store', function (pagedata,
                     utility,
                     $scope,
                     $rootScope,
                     $location,
                     $routeParams,
                     $q,
                     $route,
                     $store) {

      $scope.location = $location;


    $rootScope.urls = function(){
      var BASE_URL = '';
      if($location.host() === 'onslyde.com' || $location.host() === 'www.onslyde.com'){
        BASE_URL = 'https://onslyde.com:8443';
      }else{
        BASE_URL = 'https://localhost:8443';
      }
      return BASE_URL;
    }

//    $rootScope.userInfo = {};
    //todo - use $rootScope.registerMessage to see if there is an unfinished pres.
    if(!$rootScope.registerMessage && $store.get('registerMessage')){
      $store.bind($rootScope,'registerMessage');
    }

    if(!$rootScope.userInfo && $store.get('userInfo')){
      $store.bind($rootScope,'userInfo');
    }

//    $scope.$on('$routeChangeSuccess', function () {
//
//
//
//    });

  }]);

