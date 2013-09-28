onslyde.Services.factory('youtubeapi', function ($window, $rootScope, $log) {

  var service = $rootScope.$new(true);

  // Youtube callback when API is ready
  $window.onYouTubeIframeAPIReady = function () {
    $log.info('Youtube API is ready');
    service.ready = true;
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
    if (this.ready && this.playerId && this.videoId) {
      if(this.player) {
        this.player.destroy();
      }

      this.player = this.createPlayer();

    }
  };

  return service;
});