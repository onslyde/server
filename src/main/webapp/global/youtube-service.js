onslyde.Services.factory('youtubeapi', function ($window, $rootScope, $log, $timeout) {

  var service = $rootScope;

  // Youtube callback when API is ready
  $window.onYouTubeIframeAPIReady = function () {
    $log.info('Youtube API is ready');
    service.ready = true;
//    if (service.ready && service.playerId && service.videoId) {
//      if(service.player) {
//        service.player.destroy();
//      }
//      service.player = service.createPlayer();
//    }

  };

  service.ready = false;
  service.playerId = null;
  service.player = null;
  service.videoId = null;
  service.playerHeight = '390';
  service.playerWidth = '640';

  service.bindVideoPlayer = function (elementId) {
    $log.info('Binding to player ' + elementId);
    service.playerId = elementId;
  };

  service.createPlayer = function () {
    $log.info('Creating a new Youtube player for DOM id ' + this.playerId + ' and video ' + this.videoId);
    return new YT.Player(this.playerId, {
      height: this.playerHeight,
      width: this.playerWidth,
      videoId: this.videoId,
      events: {
        'onReady': $window.onPlayerReady
      }
    });
  };

  service.loadPlayer = function () {
    // API ready?
    $log.info('Youtube loadPlayer');
    var that = this;
    $timeout(function(){
    if (that.ready && that.playerId && that.videoId) {
      if(that.player) {
        that.player.destroy();
      }
      that.player = that.createPlayer();

      // fixed Unable to post message to https://www.youtube.com.
      // ref: https://code.google.com/p/gdata-issues/issues/detail?id=4697
//      setTimeout(function(){
//        var url = $('#analytics-player').prop('src');
//        if (url.match('^http://')) {
//          $('#analytics-player').prop('src', url.replace(/^http:\/\//i, 'https://'));
//        }
//      }, 500);
    }
    },3000);
  };

  return service;
});