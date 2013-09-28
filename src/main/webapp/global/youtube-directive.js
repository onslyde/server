onslyde.Directives.directive('youtube', [ 'youtubeapi', function (youtubeapi) {
  return {
    restrict:'A',
    link:function (scope, element) {
      console.log(element[0].id)
      youtubeapi.bindVideoPlayer(element[0].id);
    }
  };
}]);