onslyde.Directives.directive('youtube', [ 'youtubeapi', function (youtubeapi) {
  return {
    restrict:'A',
    link:function (scope, element, attrs) {
      youtubeapi.videoId = attrs.sessionid;
      youtubeapi.bindVideoPlayer(element[0].id);
      youtubeapi.loadPlayer();
    }
  };
}]);