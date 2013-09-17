onslyde.Services.factory('utility', function ($q, $http, $resource) {

  return {

    get_qs_params: function() {
      var queryParams = {};
      if (window.location.search) {
        // split up the query string and store in an associative array
        var params = window.location.search.slice(1).split("&");
        for (var i = 0; i < params.length; i++) {
          var tmp = params[i].split("=");
          queryParams[tmp[0]] = unescape(tmp[1]);
        }
      }
      return queryParams;
    },

    safeApply: function(fn) {
      var phase = this.$root.$$phase;
      if(phase == '$apply' || phase == '$digest') {
        if(fn && (typeof(fn) === 'function')) {
          fn();
        }
      } else {
        this.$apply(fn);
      }
    }
  };

})



