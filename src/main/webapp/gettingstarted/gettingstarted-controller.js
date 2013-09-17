'use strict'

onslyde.Controllers.controller('GetStartedCtrl',
  [ '$scope', '$rootScope','$location', '$http','$store', function (
                        $scope,
                        $rootScope,$location,$http,$store) {

    $scope.getstarted = {};
    $scope.viewPres = function(){

    window.open($scope.urls() + '/go/template/download?' +
      'email=' + $rootScope.userInfo.email + '&' +
      'sessionId=' + (($rootScope.registerMessage && $rootScope.registerMessage.sessionId) || '') + '&' +
      'token=' + $rootScope.userInfo.created + '&' +
      'twitter=' + $scope.getstarted.twitter + '&' +
      'presName=' + $scope.getstarted.presName + '&' +
      'poll1=' + $scope.getstarted.poll1 + '&' +
      'option1=' + $scope.getstarted.option1 + '&' +
      'option2=' + $scope.getstarted.option2);

    //need a better check to make sure pres was created, or show it as created in ui
    $store.remove('registerMessage')

    }

    $scope.setupPres = function(){

      $scope.getstarted.email = $rootScope.userInfo.email;
      //if we send empty, it will create new
      $scope.getstarted.sessionId = (($rootScope.registerMessage && $rootScope.registerMessage.sessionId) || '');
      $scope.getstarted.token = $rootScope.userInfo.created;

      $http({method: 'POST', url: $scope.urls() + '/go/template/create', data: $scope.getstarted, headers: {}}).
        success(function(data, status, headers, config) {
//          console.log(data)
          var frame = getFrame();
          frame.write(data);
          frame.location = '#';

        }).
        error(function(data, status, headers, config) {
          console.log('error', data);
        });

      //need a better check to make sure pres was created, or show it as created in ui
    $store.remove('registerMessage')

      var getFrame = function () {

        var frame = document.getElementById("temp-frame");
        if (!frame) {
          // create frame
          frame = document.createElement("iframe");
          frame.setAttribute("id", "temp-frame");
          frame.setAttribute("name", "temp-frame");
          frame.setAttribute("seamless", "");
          frame.setAttribute("sandbox", "allow-same-origin allow-scripts allow-popups allow-forms");
          frame.setAttribute("height","768px")
          frame.setAttribute("width","100%");
          frame.setAttribute("allowFullScreen","");
//          frame.setAttribute("onload","setTimeout(function(){window.slidfast({onslyde: {deck:true,sessionID: 224, mode:'reveal'}})},1000)");

          document.getElementById('preview-container').appendChild(frame);
        }
        // load a page
        return frame.contentDocument;
      };



    }

    $scope.downloadPres = function(){

      $scope.getstarted.email = $rootScope.userInfo.email;
      //if we send empty, it will create new
      $scope.getstarted.sessionId = (($rootScope.registerMessage && $rootScope.registerMessage.sessionId) || '');
      $scope.getstarted.token = $rootScope.userInfo.created;

      $http({method: 'GET', url: $scope.urls() + '/go/template/download', data: $scope.getstarted, headers: {}}).
        success(function(data, status, headers, config) {
          console.log(data)
        }).
        error(function(data, status, headers, config) {
          console.log('error', data);
        });

      //need a better check to make sure pres was created, or show it as created in ui
      $store.remove('registerMessage')
    }


  }]);

