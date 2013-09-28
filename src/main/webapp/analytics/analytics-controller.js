'use strict'

onslyde.Controllers.controller('AnalyticsCtrl',
  ['pagedata',
    'chartservice',
    '$scope',
    '$rootScope',
    '$routeParams',
    '$timeout', 'youtubeapi', '$window', function (pagedata, chartservice, $scope, $rootScope, $routeParams, $timeout, youtubeapi, $window) {

    $scope.analyticsSetup = function () {


      function createGradient(color1, color2) {
        var perShapeGradient = {
          x1:0,
          y1:0,
          x2:0,
          y2:1
        };
        return {
          linearGradient:perShapeGradient,
          stops:[
            [0, color1],
            [1, color2]
          ]
        };
      }

      var dataDescription = {
        timeseries:{
          yAxisLabels: [''],
          labels:['Total'],
          dataAttr:['label', ['timestamp','count']],
          colors:['rgba(43,166,203,0.9)',
                  'rgba(52,52,52,0.9)'],
          borderColor:'#1b97d1'
        },
        pie: {
          dataAttr: ['label', ['timestamp','count']],
          colors: [
            'rgba(43,166,203,0.4)',
            'rgba(52,52,52,0.3)',
            'rgba(146,223,129,0.5)',
            'rgba(146,223,129,0.5)',
            'rgba(146,223,129,0.5)',
            '#ff9191', '#ffa1a1', '#ffb6b6', '#ffcbcb'],
          borderColor: '#ff0303'
        }
      };

      if (!$rootScope.chartTemplate) {
        //get the chart template for this view... right now it covers all charts...
        pagedata.get(null, 'charts/highcharts.json').then(function (success) {
          $rootScope.chartTemplate = success;


          pagedata.get(null, '/go/analytics/' + $routeParams.sessionID).then(function (success) {
            $rootScope.sessionData = success;


            youtubeapi.videoId = $rootScope.sessionData.sessionCode;
            youtubeapi.loadPlayer();

//              function(){
//              setTimeout(function(){
//                if(!$scope.$$phase) {
//                  $scope.$apply(function () {
//                    youtubeapi.player.seekTo(0);
//                  })
//                }
//
//            },10000);
//            });
//            $window.onPlayerReady = function(){

//
//            }



            var allVotes = [
              {label:'',datapoints:[]},
              {label:'',datapoints:[]}
            ];
//            console.log($rootScope.sessionData);

            $scope.twoOptionsList = [];
            $scope.twoOptionsList.totals = {agree: 1, disagree: 1};

            angular.forEach($scope.sessionData.slideGroups, function(value, index){


              var options = [
                {label:'',datapoints:[]},
                {label:'',datapoints:[]},
                {label:'',datapoints:[]},
                {label:'',datapoints:[]}
              ];


              var twooptions = [
                {label:'',datapoints:[]},
                {label:'',datapoints:[]}
              ];

              twooptions.created = value.created;
              twooptions.topicName = value.slides[0].slideIndex;
              twooptions.topicID = value.slides[0].id;

              //if we have atleast 1 vote on the topic
              if(value.slides[0].slideVoteses.length > 2){

                var optionTracker = {},
                  slideOptions = value.slides[0].slideOptionses;


                //get list of available options
                for(var i=0;i<slideOptions.length;i++){
                  twooptions[i].label = slideOptions[i].name;
                  allVotes[i].label = slideOptions[i].name;
                  optionTracker[slideOptions[i].name] = 0;
                  twooptions[i].datapoints.push({"timestamp":0,"count":0});
                }


                //get the votes and attendee data
                angular.forEach(value.slides[0].slideVoteses, function(vote, toindex){
                  var voteTime = vote.voteTime,
                    attendee = vote.attendee;

                  //loop through all options for compare... this is so we can display a growth chart and not just spikes when votes occur
                  for(var i=0;i<twooptions.length;i++){
                    var
                      dataPoints = twooptions[i].datapoints,
                      totalLength = dataPoints[dataPoints.length-1],
                      lastValue;


                     //if we find a vote, fill it in to the approriate array
                    //todo - cleanup repetitive code
                    if(twooptions[i].label === vote.slideOptions.name){

                      if(optionTracker[twooptions[i].label] === 0  && totalLength.timestamp === 0){
                        twooptions[i].datapoints[0] = {"timestamp":voteTime,"count":1};
                        optionTracker[twooptions[i].label] = 1;
                      }else{
                        if(optionTracker[twooptions[i].label] === 0){
                          optionTracker[twooptions[i].label] = 1;
                        }
                        twooptions[i].datapoints.push({"timestamp":voteTime,"count":optionTracker[twooptions[i].label]++});
                      }
                      $scope.twoOptionsList.totals[twooptions[i].label] += 1;

                      allVotes[i].datapoints.push({"timestamp":voteTime,"count":1});

                    //otherwise populate with 0, or the last known count for the opposite label
                    }else{

                      if(optionTracker[twooptions[i].label] === 0 && totalLength.timestamp === 0){
                        twooptions[i].datapoints[0] = {"timestamp":voteTime,"count":optionTracker[twooptions[i].label]};

                      }else{
                        twooptions[i].datapoints.push({"timestamp":voteTime,"count":optionTracker[twooptions[i].label]});

                      }
                      allVotes[i].datapoints.push({"timestamp":voteTime,"count":0});

                    }

                    //sort the datapoints based on timestamps
                    twooptions[i].datapoints.sort(function(a, b){
                      a = a['timestamp'];
                      b = b['timestamp'];
                      return a > b ? 1 : a < b ? -1 : 0;
                    });




                  }

                });

                //sort the labels alphabetically
                twooptions.sort(function(a, b){
                  a = a['label'].toLowerCase();
                  b = b['label'].toLowerCase();
                  return a > b ? 1 : a < b ? -1 : 0;
                });


                var startTime = $rootScope.sessionData.end;
                var tempLineChart = angular.copy($rootScope.chartTemplate.line);
                var tempPieChart = angular.copy($rootScope.chartTemplate.pie);

                twooptions.chartData = chartservice.convertLineChart(twooptions, tempLineChart, dataDescription.timeseries, '');
                twooptions.pieChartData = chartservice.convertPieChart(twooptions, tempPieChart, dataDescription.pie, '');

//                youtubeapi.bindVideoPlayer('analytics-player');

                var createClickable = function(series) {

                  series.point = {events: {
                    click: function (event) {
//                      var d = new Date(this.category - startTime);
                      $('#chart_movie').foundation('reveal', 'open');
                      $('#chart_movie').bind('close', function() {
                        youtubeapi.player.pauseVideo();
                      });
//                      document.getElementById('chart-movie-frame').src = 'http://www.youtube.com/v/' + $rootScope.sessionData.sessionCode + '?version=3&autoplay=1&start=' + ((this.category - startTime) / 1000);
                      youtubeapi.player.seekTo(((this.category - startTime) / 1000));
                      youtubeapi.player.playVideo();
                    }
                  }
                  };
                }

                createClickable(twooptions.chartData.series[0]);
                createClickable(twooptions.chartData.series[1]);
                $scope.twoOptionsList.push(twooptions);

              }
            });

//            var tempPieChart = angular.copy($rootScope.chartTemplate.pie);
//            allVotes.sort(function(a, b){
//              a = a['label'].toLowerCase();
//              b = b['label'].toLowerCase();
//              return a > b ? 1 : a < b ? -1 : 0;
//            });
//            $scope.twoOptionsList.overviewChart = chartservice.convertPieChart(allVotes, tempPieChart, dataDescription.pie, '');



          }, function (fail) {
            console.log('Problem getting chart datapoints', fail)
          });

        }, function (fail) {
          console.log('Problem getting chart template', fail)
        });
      }

    };


  }]);