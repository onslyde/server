'use strict'

onslyde.Controllers.controller('AnalyticsCtrl',
    ['pagedata',
      'chartservice',
      '$scope',
      '$rootScope',
      '$routeParams',
      '$timeout', 'youtubeapi', '$q', function (pagedata, chartservice, $scope, $rootScope, $routeParams, $timeout, youtubeapi, $q) {

      $scope.sessionID = $routeParams.sessionID;

      $scope.presAnalyticsSetup = function () {
        pagedata.get(null, $rootScope.urls() + '/go/analytics/list/' + $rootScope.userInfo.id).then(function (success) {
          $scope.sessionList = success;
        }, function (fail) {
          console.log('Problem getting session list', fail)
        });


      };

      $scope.panelAnalyticsSetup = function () {

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
            yAxisLabels:[''],
            labels:['Total'],
            dataAttr:['label', ['timestamp', 'count']],
            colors:['rgba(43,166,203,0.9)',
              'rgba(52,52,52,0.3)',
              'rgba(169,234,181,0.9)',
              'rgba(43,43,43,0.5)'],
            borderColor:'#1b97d1'
          },
          pie:{
            dataAttr:['label', ['timestamp', 'count']],
            colors:[
              'rgba(43,166,203,0.4)',
              'rgba(52,52,52,0.3)',
              'rgba(169,234,181,0.5)',
              'rgba(43,43,43,0.5)',
              '#ff9191', '#ffa1a1', '#ffb6b6', '#ffcbcb'],
            borderColor:'#ff0303'
          }
        };


        $scope.createCharts = function () {
          pagedata.get(null, $rootScope.urls() + '/go/analytics/' + $routeParams.sessionID).then(function (success) {
            $rootScope.sessionData = success;


            $scope.twoOptionsList = [];

            $scope.dashBoard = {};
            $scope.dashBoard.totals = {agree:1, disagree:1};
            $scope.dashBoard.speakerTotals = [];
            $scope.dashBoard.sessionVotesFilterList =
                [
                  {value:1},
                  {value:2},
                  {value:3},
                  {value:4},
                  {value:5},
                  {value:6},
                  {value:7},
                  {value:8},
                  {value:9},
                  {value:10}
                ];

            $scope.dashBoard.sessionVotesFilter = $scope.dashBoard.sessionVotesFilterList[1];

            $scope.$watch('dashBoard.sessionVotesFilter', function (newVal, oldVal) {
              if (newVal !== oldVal) {
                $scope.twoOptionsList = [];
                $scope.dashBoard.totals = {agree:1, disagree:1};
                $scope.dashBoard.speakerTotals = [];
                createCharts();
              }
            });


            createCharts();

            function createCharts() {
              var overViewOptions = [];

              angular.forEach($scope.sessionData.slideGroups, function (value, index) {

                var twooptions,
                    voteData,
                    voteOptions,
                    allVotes;

                if (value.slideGroupOptionses.length > 2) {

                  twooptions = [
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]}
                  ];

                  allVotes = [
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]}
                  ];

                  voteData = value.slideGroupVoteses;
                  voteOptions = value.slideGroupOptionses;
                  twooptions.topicName = value.groupName;

                  try {
                    twooptions.topicImage = value.slides[0].screenshot;
                  } catch (e) {
                  }
                } else {

                  twooptions = [
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]}
                  ];

                  allVotes = [
                    {label:'', datapoints:[]},
                    {label:'', datapoints:[]}
                  ];
                  if (typeof value.slides[0] !== 'undefined') {
                    voteData = value.slides[0].slideVoteses;
                    voteOptions = value.slides[0].slideOptionses;
                    twooptions.yo = value.slides[0];
                    twooptions.topicName = value.slides[0].slideIndex;
                    twooptions.topicImage = value.slides[0].screenshot;
                    twooptions.topicID = value.slides[0].id;
                  }
                }


                twooptions.created = value.created;


                twooptions.sessionID = $routeParams.sessionID;

                twooptions.speakerData = $scope.getPanelist($routeParams.sessionID, twooptions.topicName);

                //if we have atleast 1 vote on the topic
                if (voteData) {
                  if (voteData.length >= $scope.dashBoard.sessionVotesFilter.value) {

                    // summary data for speaker display at top
                    var spearkerStat = getSpeakerSummaryData($scope.dashBoard.speakerTotals, twooptions);

                    var optionTracker = {},
                        slideOptions = voteOptions;

                    //get list of available options
                    for (var i = 0; i < slideOptions.length; i++) {
                      twooptions[i].label = slideOptions[i].name;
                      allVotes[i].label = slideOptions[i].name;
                      optionTracker[slideOptions[i].name] = 0;
                      twooptions[i].datapoints.push({"timestamp":0, "count":0});
                    }


                    //get the votes and attendee data for agree/disagree ONLY options
                    angular.forEach(voteData, function (vote, toindex) {
                      var voteTime = vote.voteTime,
                          attendee = vote.attendee,
                          thisSlideOptions;

                      if (twooptions.length === 2) {
                        thisSlideOptions = vote.slideOptions;
                      } else {
                        thisSlideOptions = vote.slideGroupOptions;
                      }

                      //loop through all options for compare... this is so we can display a growth chart and not just spikes when votes occur
                      for (var i = 0; i < twooptions.length; i++) {
                        var
                            dataPoints = twooptions[i].datapoints,
                            totalLength = dataPoints[dataPoints.length - 1],
                            lastValue;


                        //if we find a vote, fill it in to the approriate array
                        //todo - cleanup repetitive code
                        if (twooptions[i].label === thisSlideOptions.name) {

                          if (optionTracker[twooptions[i].label] === 0 && totalLength.timestamp === 0) {
                            twooptions[i].datapoints[0] = {"timestamp":voteTime, "count":1};
                            optionTracker[twooptions[i].label] = 1;
                          } else {
                            if (optionTracker[twooptions[i].label] === 0) {
                              optionTracker[twooptions[i].label] = 1;
                            }
                            //increment the option tracker for this label by 1
                            twooptions[i].datapoints.push({"timestamp":voteTime, "count":optionTracker[twooptions[i].label]++});
                          }
                          //increment total count for summary
                          $scope.dashBoard.totals[twooptions[i].label] += 1;

                          allVotes[i].datapoints.push({"timestamp":voteTime, "count":1});

                          //otherwise populate with 0, or the last known count for the opposite label
                        } else {

                          if (optionTracker[twooptions[i].label] === 0 && totalLength.timestamp === 0) {
                            twooptions[i].datapoints[0] = {"timestamp":voteTime, "count":optionTracker[twooptions[i].label]};
                          } else {
                            twooptions[i].datapoints.push({"timestamp":voteTime, "count":optionTracker[twooptions[i].label]});
                          }
                          allVotes[i].datapoints.push({"timestamp":voteTime, "count":0});

                        }

                        //sort the datapoints based on timestamps
                        twooptions[i].datapoints.sort(function (a, b) {
                          a = a['timestamp'];
                          b = b['timestamp'];
                          return a > b ? 1 : a < b ? -1 : 0;
                        });


                      }

                    });


                    //increment total count for speaker
                    for (var i = 0; i < twooptions.length; i++) {
                      spearkerStat[twooptions[i].label] += twooptions[i].datapoints[twooptions[i].datapoints.length - 1].count;
                    }
                    spearkerStat.sessions += 1;
                    $scope.dashBoard.speakerTotals.push(spearkerStat);

                    if(twooptions){
                      overViewOptions.push(twooptions[0]);
                      overViewOptions.push(twooptions[1]);
                    }

                    var startTime = $rootScope.sessionData.end;
                    var tempLineChart = angular.copy($rootScope.chartTemplate.line);
                    var tempPieChart = angular.copy($rootScope.chartTemplate.pie);

                    twooptions.chartData = chartservice.convertLineChart(twooptions, tempLineChart, dataDescription.timeseries, '');
                    twooptions.pieChartData = chartservice.convertPieChart(twooptions, tempPieChart, dataDescription.pie, '');



                    var createYoutubeClickable = function (series) {

                      series.point = {events:{
                        click:function (event) {
//                      var d = new Date(this.category - startTime);
                          $('#chart_movie').foundation('reveal', 'open');
                          $('#chart_movie').bind('close', function () {
                            youtubeapi.player.pauseVideo();
                          });
//                      document.getElementById('chart-movie-frame').src = 'http://www.youtube.com/v/' + $rootScope.sessionData.sessionCode + '?version=3&autoplay=1&start=' + ((this.category - startTime) / 1000);
                          youtubeapi.player.seekTo(((this.category - startTime) / 1000));
                          youtubeapi.player.playVideo();
                        }
                      }
                      };
                    }

                    if ($rootScope.sessionData.sessionCode && $rootScope.sessionData.extra === 'panel') {
                      createYoutubeClickable(twooptions.chartData.series[0]);
                      createYoutubeClickable(twooptions.chartData.series[1]);
                    }
                    $scope.twoOptionsList.push(twooptions);


                  }
                }
              });
              var tempOverViewChart = angular.copy($rootScope.chartTemplate.line);
              $scope.overViewOptions = chartservice.convertLineChart(overViewOptions, tempOverViewChart, dataDescription.timeseries, '');
            }

          }, function (fail) {
            console.log('Problem getting chart datapoints', fail)
          });

        };

        function getSpeakerSummaryData(speakerTotals, twooptions) {
          var spearkerStat = {topic:twooptions.topicName, agree:0, disagree:0, sessions:0, speaker:twooptions.speakerData};

          if (speakerTotals.length > 0) {
            for (var d = 0; d < speakerTotals.length; d++) {
              if (speakerTotals[d].topic === twooptions.topicName) {
                spearkerStat = speakerTotals.splice(d, 1)[0];
                //topic does exist so break
                break;
              }
            }
          }

          return spearkerStat;

        }


        if (!$rootScope.chartTemplate) {
          //get the chart template for this view... right now it covers all charts...
          pagedata.get(null, 'charts/highcharts.json').then(function (success) {
            $rootScope.chartTemplate = success;
            $scope.createCharts();
          }, function (fail) {
            console.log('Problem getting chart template', fail)
          });
        } else {
          $scope.createCharts();
        }

        var panelistjson;

        //todo - this is a temporary lookup for edge. need to add columns to attendee db for pic and twitter
        $scope.getPanelist = function (sessionID, name) {

          var result,allpanels,
              deferred = $q.defer();

          if(!panelistjson){
            pagedata.get(null, '/analytics/json/edge/edge1.json').then(function (success) {
              panelistjson = success.panelists;
              deferred.resolve(success.panelists);
            }, function (fail) {
              deferred.reject(fail);
            });
          }


          deferred.promise.then(function(){
              angular.forEach(allpanels, function (value, index) {
                if (value.name === name) {
                  result = value;
                }
              });
              return result;
              }
          )

        };

      };


    }]);