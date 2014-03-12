'use strict';
var onslyde = onslyde || {};
onslyde.Constants = angular.module('onslyde.constants', []);
onslyde.Services = angular.module('onslyde.services', []);
onslyde.Controllers = angular.module('onslyde.controllers', []);
onslyde.Filters = angular.module('onslyde.filters', []);
onslyde.Directives = angular.module('onslyde.directives', []);
angular.module('onslyde', [
  'ngResource',
  'ngRoute',
  'localStorage',
  'onslyde.filters',
  'onslyde.services',
  'onslyde.directives',
  'onslyde.constants',
  'onslyde.controllers'
]).config([
  '$routeProvider',
  '$locationProvider',
  function ($routeProvider, $locationProvider) {
    $routeProvider.when('/home', {
      templateUrl: 'home/home.html',
      controller: 'PageCtrl'
    }).when('/login', {
      templateUrl: 'global/login.html',
      controller: 'LoginCtrl'
    }).when('/gettingstarted', {
      templateUrl: 'gettingstarted/gettingstarted.html',
      controller: 'GetStartedCtrl'
    }).when('/register', {
      templateUrl: 'home/register.html',
      controller: 'PageCtrl'
    }).when('/analytics', {
      templateUrl: 'analytics/analytics.html',
      controller: 'AnalyticsCtrl'
    }).when('/analytics/edge2013', {
      templateUrl: 'analytics/edgeNY-2013.html',
      controller: 'AnalyticsCtrl'
    }).otherwise({ redirectTo: '/home' });
    $locationProvider.html5Mode(false).hashPrefix('!');
  }
]).run(function ($rootScope) {
  $rootScope.$on('$viewContentLoaded', function () {
    angular.element(document).ready(function () {
      var tag = document.createElement('script');
      tag.src = '//www.youtube.com/iframe_api';
      var firstScriptTag = document.getElementsByTagName('script')[0];
      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
      //very, very fugly
      window.runFoundation(window, document, undefined);
      window.runOrbit(window, document, undefined);
      window.runReveal(window, document, undefined);
      window.runTooltip(window, document, undefined);
      $(document).foundation();
      $('#how-can-use').on('orbit:after-slide-change', function (event, orbit) {
        //          console.info("after slide change");
        //          console.info("slide ", document.querySelectorAll('.slide'));
        var counter = 1;
        angular.forEach(document.querySelectorAll('.slide'), function (value, key) {
          if (orbit.slide_number + 1 === counter) {
            value.classList.remove('hidden');
            value.classList.add('slide' + counter);
          } else {
            value.classList.add('hidden');
            value.classList.remove('slide' + counter);
          }
          counter++;
        });
      });
    });  //      $('#sign-up').on('invalid', function () {
         //        alert('np')
         //      })
         //        .on('valid', function () {
         //          alert('yes')
         //        });
         //      });
  });
});
'use strict';
onslyde.Controllers.controller('AnalyticsCtrl', [
  'pagedata',
  'chartservice',
  '$scope',
  '$rootScope',
  '$routeParams',
  '$timeout',
  'youtubeapi',
  '$location',
  '$anchorScroll',
  function (pagedata, chartservice, $scope, $rootScope, $routeParams, $timeout, youtubeapi, $location, $anchorScroll) {
    $scope.sessionID = $routeParams.sessionID;
    $scope.presAnalyticsSetup = function () {
      pagedata.get(null, $rootScope.urls() + '/go/analytics/list/' + $rootScope.userInfo.id).then(function (success) {
        $scope.sessionList = success;
      }, function (fail) {
        console.log('Problem getting session list', fail);
      });
    };
    $scope.panelAnalyticsSetup = function () {
      function createGradient(color1, color2) {
        var perShapeGradient = {
            x1: 0,
            y1: 0,
            x2: 0,
            y2: 1
          };
        return {
          linearGradient: perShapeGradient,
          stops: [
            [
              0,
              color1
            ],
            [
              1,
              color2
            ]
          ]
        };
      }
      var dataDescription = {
          timeseries: {
            yAxisLabels: [''],
            labels: ['Total'],
            dataAttr: [
              'label',
              [
                'timestamp',
                'count'
              ]
            ],
            colors: [
              'rgba(43,166,203,0.9)',
              'rgba(52,52,52,0.3)',
              'rgba(169,234,181,0.9)',
              'rgba(43,43,43,0.5)'
            ],
            borderColor: '#1b97d1'
          },
          pie: {
            dataAttr: [
              'label',
              [
                'timestamp',
                'count'
              ]
            ],
            colors: [
              'rgba(43,166,203,0.4)',
              'rgba(52,52,52,0.3)',
              'rgba(169,234,181,0.5)',
              'rgba(43,43,43,0.5)',
              '#ff9191',
              '#ffa1a1',
              '#ffb6b6',
              '#ffcbcb'
            ],
            borderColor: '#ff0303'
          }
        };
      $scope.createCharts = function () {
        pagedata.get(null, $rootScope.urls() + '/go/analytics/' + $routeParams.sessionID).then(function (success) {
          $rootScope.sessionData = success;
          $scope.twoOptionsList = [];
          $scope.dashBoard = {};
          $scope.dashBoard.totals = {
            agree: 1,
            disagree: 1
          };
          $scope.dashBoard.speakerTotals = [];
          $scope.dashBoard.sessionVotesFilterList = [
            { value: 1 },
            { value: 2 },
            { value: 3 },
            { value: 4 },
            { value: 5 },
            { value: 6 },
            { value: 7 },
            { value: 8 },
            { value: 9 },
            { value: 10 }
          ];
          $scope.dashBoard.sessionVotesFilter = $scope.dashBoard.sessionVotesFilterList[1];
          $scope.$watch('dashBoard.sessionVotesFilter', function (newVal, oldVal) {
            if (newVal !== oldVal) {
              $scope.twoOptionsList = [];
              $scope.dashBoard.totals = {
                agree: 1,
                disagree: 1
              };
              $scope.dashBoard.speakerTotals = [];
              createCharts();
            }
          });
          createCharts();
          function createCharts() {
            angular.forEach($scope.sessionData.slideGroups, function (value, index) {
              var twooptions, voteData, voteOptions, allVotes;
              if (value.slideGroupOptionses.length > 2) {
                twooptions = [
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  }
                ];
                allVotes = [
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  }
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
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  }
                ];
                allVotes = [
                  {
                    label: '',
                    datapoints: []
                  },
                  {
                    label: '',
                    datapoints: []
                  }
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
                  var optionTracker = {}, slideOptions = voteOptions;
                  //get list of available options
                  for (var i = 0; i < slideOptions.length; i++) {
                    twooptions[i].label = slideOptions[i].name;
                    allVotes[i].label = slideOptions[i].name;
                    optionTracker[slideOptions[i].name] = 0;
                    twooptions[i].datapoints.push({
                      'timestamp': 0,
                      'count': 0
                    });
                  }
                  //get the votes and attendee data for agree/disagree ONLY options
                  angular.forEach(voteData, function (vote, toindex) {
                    var voteTime = vote.voteTime, attendee = vote.attendee, thisSlideOptions;
                    if (twooptions.length === 2) {
                      thisSlideOptions = vote.slideOptions;
                    } else {
                      thisSlideOptions = vote.slideGroupOptions;
                    }
                    //loop through all options for compare... this is so we can display a growth chart and not just spikes when votes occur
                    for (var i = 0; i < twooptions.length; i++) {
                      var dataPoints = twooptions[i].datapoints, totalLength = dataPoints[dataPoints.length - 1], lastValue;
                      //if we find a vote, fill it in to the approriate array
                      //todo - cleanup repetitive code
                      if (twooptions[i].label === thisSlideOptions.name) {
                        if (optionTracker[twooptions[i].label] === 0 && totalLength.timestamp === 0) {
                          twooptions[i].datapoints[0] = {
                            'timestamp': voteTime,
                            'count': 1
                          };
                          optionTracker[twooptions[i].label] = 1;
                        } else {
                          if (optionTracker[twooptions[i].label] === 0) {
                            optionTracker[twooptions[i].label] = 1;
                          }
                          //increment the option tracker for this label by 1
                          twooptions[i].datapoints.push({
                            'timestamp': voteTime,
                            'count': optionTracker[twooptions[i].label]++
                          });
                        }
                        //increment total count for summary
                        $scope.dashBoard.totals[twooptions[i].label] += 1;
                        allVotes[i].datapoints.push({
                          'timestamp': voteTime,
                          'count': 1
                        });  //otherwise populate with 0, or the last known count for the opposite label
                      } else {
                        if (optionTracker[twooptions[i].label] === 0 && totalLength.timestamp === 0) {
                          twooptions[i].datapoints[0] = {
                            'timestamp': voteTime,
                            'count': optionTracker[twooptions[i].label]
                          };
                        } else {
                          twooptions[i].datapoints.push({
                            'timestamp': voteTime,
                            'count': optionTracker[twooptions[i].label]
                          });
                        }
                        allVotes[i].datapoints.push({
                          'timestamp': voteTime,
                          'count': 0
                        });
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
                  var startTime = $rootScope.sessionData.end;
                  var tempLineChart = angular.copy($rootScope.chartTemplate.line);
                  var tempPieChart = angular.copy($rootScope.chartTemplate.pie);
                  twooptions.chartData = chartservice.convertLineChart(twooptions, tempLineChart, dataDescription.timeseries, '');
                  twooptions.pieChartData = chartservice.convertPieChart(twooptions, tempPieChart, dataDescription.pie, '');
                  var createYoutubeClickable = function (series) {
                    series.point = {
                      events: {
                        click: function (event) {
                          //                      var d = new Date(this.category - startTime);
                          $('#chart_movie').foundation('reveal', 'open');
                          $('#chart_movie').bind('close', function () {
                            youtubeapi.player.pauseVideo();
                          });
                          //                      document.getElementById('chart-movie-frame').src = 'http://www.youtube.com/v/' + $rootScope.sessionData.sessionCode + '?version=3&autoplay=1&start=' + ((this.category - startTime) / 1000);
                          youtubeapi.player.seekTo((this.category - startTime) / 1000);
                          youtubeapi.player.playVideo();
                        }
                      }
                    };
                  };
                  if ($rootScope.sessionData.sessionCode && $rootScope.sessionData.sessionCode !== 'beta') {
                    createYoutubeClickable(twooptions.chartData.series[0]);
                    createYoutubeClickable(twooptions.chartData.series[1]);
                  }
                  $scope.twoOptionsList.push(twooptions);
                }
              }
            });
          }
          //todo change timeout to after angular document loaded
          if ($location.hash()) {
            $timeout(function () {
              $anchorScroll($location.hash());
            }, 2000);
          }
        }, function (fail) {
          console.log('Problem getting chart datapoints', fail);
        });
      };
      function getSpeakerSummaryData(speakerTotals, twooptions) {
        var spearkerStat = {
            topic: twooptions.topicName,
            agree: 0,
            disagree: 0,
            sessions: 0,
            speaker: twooptions.speakerData
          };
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
          console.log('Problem getting chart template', fail);
        });
      } else {
        $scope.createCharts();
      }
      //todo - this is a temporary lookup for edge. need to add columns to attendee db for pic and twitter
      $scope.getPanelist = function (sessionID, name) {
        var result;
        var allpanels = [
            {
              name: 'Natasha Rooney',
              org: 'GSMA',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/natasha-rooney.jpg',
              twitter: 'thisnatasha',
              mod: true
            },
            {
              name: 'Manu Sporny',
              org: 'Digital Bazaar',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/manu-sporny.jpg',
              twitter: 'manusporny'
            },
            {
              name: 'Rob Grimshaw',
              org: 'FT.com',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/rob-grimshaw.jpg',
              twitter: 'r_g'
            },
            {
              name: 'Cyndy Lobb',
              org: 'Google',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/cyndy-lobb.jpg',
              twitter: ''
            },
            {
              name: 'Ricardo Varela',
              org: 'Telef\xf3nica',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/ricardo-varela.jpg',
              twitter: 'phobeo'
            },
            {
              name: 'Kumar McMillan',
              org: 'Mozilla',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/kumar-mcmillan.jpg',
              twitter: 'kumar303'
            },
            {
              name: 'Marcos Caceres',
              org: 'Mozilla',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/marcos-caceres.jpg',
              twitter: 'marcosc',
              mod: true
            },
            {
              name: 'Yoav Weiss',
              org: 'WL Square',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/yoav-weiss.jpg',
              twitter: 'yoavweiss'
            },
            {
              name: 'Ann Robson',
              org: 'freelance',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/ann-robson.jpg',
              twitter: 'arobson'
            },
            {
              name: 'Estelle Weyl',
              org: 'freelance',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/estelle-weyl.jpg',
              twitter: 'estellevw'
            },
            {
              name: 'Peter Miller',
              org: 'Conde Nast',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/peter-miller.jpg',
              twitter: 'petemill'
            },
            {
              name: 'John Mellor',
              org: 'Google',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/john-mellor.jpg',
              twitter: ''
            },
            {
              name: 'Andre Behrens',
              org: 'The New York Times',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/andre-behrens.jpg',
              twitter: 'mrandre',
              mod: true
            },
            {
              name: 'Jonathan Klein',
              org: 'Etsy',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/jonathan-klein.jpg',
              twitter: 'jonathanklein'
            },
            {
              name: 'Paul Lewis',
              org: 'Google',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/paul-lewis.jpg',
              twitter: 'aerotwist'
            },
            {
              name: 'Ariya Hidayat',
              org: 'Sencha',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/ariya-hidayat.jpg',
              twitter: 'ariyahidayat'
            },
            {
              name: 'Joshua Peek',
              org: 'Github',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/joshua-peek.jpg',
              twitter: 'joshpeek'
            },
            {
              name: 'Eli Fidler',
              org: 'BlackBerry',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/eli-fidler.jpg',
              twitter: 'efidler'
            },
            {
              name: 'Scott Jenson',
              org: 'Jenson Design',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/scott-jenson.jpg',
              twitter: 'scottjenson',
              mod: true
            },
            {
              name: 'Martyn Loughran',
              org: 'Pusher',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/martyn-loughran.jpg',
              twitter: 'mloughran'
            },
            {
              name: 'Wesley Hales',
              org: 'Apigee',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/wesley-hales.jpg',
              twitter: 'wesleyhales'
            },
            {
              name: 'Rob Hawkes',
              org: 'freelance',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/rob-hawkes.jpg',
              twitter: 'robhawkes'
            },
            {
              name: 'John Fallows',
              org: 'Kaazing',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/john-fallows.jpg',
              twitter: ''
            },
            {
              name: 'Henrik Joretag',
              org: '&yet',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/henrik-joretag.jpg',
              twitter: 'HenrikJoreteg'
            },
            {
              name: 'Paul Irish',
              org: 'Google',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/paul-irish.jpg',
              twitter: 'paul_irish',
              mod: true
            },
            {
              name: 'Tom Maslen',
              org: 'BBC',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/tom-maslen.jpg',
              twitter: 'tmaslen'
            },
            {
              name: 'Tomomi Imura',
              org: 'Nokia',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/tomomi-imura.jpg',
              twitter: 'girlie_mac'
            },
            {
              name: 'Shwetank Dixit',
              org: 'Opera',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/shwetank-dixit.jpg',
              twitter: 'shwetank'
            },
            {
              name: 'Edd Sowden',
              org: 'Government Digital Service',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/edd-sowden.jpg',
              twitter: 'edds'
            },
            {
              name: 'Steve Thair',
              org: 'Seriti Consulting',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/steve-thair.jpg',
              twitter: 'theopsmgr',
              mod: true
            },
            {
              name: 'Ben Vinegar',
              org: 'Disqus',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/ben-vinegar.jpg',
              twitter: 'bentlegen'
            },
            {
              name: 'Guy Podjarny',
              org: 'Akamai',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/guy-podjarny.jpg',
              twitter: 'guypod'
            },
            {
              name: 'Stoyan Stefanov',
              org: 'Facebook',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/stoyan-stefanov.jpg',
              twitter: 'stoyanstefanov'
            },
            {
              name: 'Barbara Bermes',
              org: 'CBC',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/barbara-bermes.jpg',
              twitter: 'bbinto'
            },
            {
              name: 'Jake Archibald',
              org: 'Google',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/jake-archibald.jpg',
              twitter: 'jaffathecake',
              mod: true
            },
            {
              name: 'Alex Russell',
              org: 'Google',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/alex-russell.jpg',
              twitter: 'slightlylate'
            },
            {
              name: 'Matt Andrews',
              org: 'FT Labs',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/matt-andrews.jpg',
              twitter: 'andrewsmatt'
            },
            {
              name: 'Craig Cavalier',
              org: 'LiquidFrameworks',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/craig-cavalier.jpg',
              twitter: 'CraigCav'
            },
            {
              name: 'Calvin Spealman',
              org: 'Caktus Consulting',
              pic: 'http://edgeconf.com/2013-nyc/images/panelists/calvin-spealman.jpg',
              twitter: 'ironfroggy'
            }
          ];
        angular.forEach(allpanels, function (value, index) {
          if (value.name === name) {
            result = value;
          }
        });
        return result;
      };
    };
  }
]);
'use strict';
onslyde.Directives.directive('chart', function ($rootScope) {
  return {
    restrict: 'E',
    scope: { chartdata: '=chartdata' },
    template: '<div></div>',
    replace: true,
    controller: function ($scope, $element) {
    },
    link: function (scope, element, attrs) {
      scope.$watch('chartdata', function (chartdata, oldchartdata) {
        if (chartdata) {
          //set chart defaults through tag attributes
          var chartsDefaults = {
              chart: {
                renderTo: element[0],
                type: attrs.type || null,
                height: attrs.height || null,
                width: attrs.width || null,
                reflow: true,
                animation: false,
                zoomType: 'x'
              }
            };
          if (attrs.type === 'pie') {
            chartsDefaults.chart.margin = [
              0,
              0,
              0,
              0
            ];
            chartsDefaults.chart.spacingLeft = 0;
            chartsDefaults.chart.spacingRight = 0;
            chartsDefaults.chart.spacingTop = 0;
            chartsDefaults.chart.spacingBottom = 0;
            if (attrs.titleimage) {
              chartdata.title.text = '<img src="' + attrs.titleimage + '">';
            }
            if (attrs.titleicon) {
              chartdata.title.text = '<i class="pictogram title">' + attrs.titleicon + '</i>';
            }
            if (attrs.titlecolor) {
              chartdata.title.style.color = attrs.titlecolor;
            }
            if (attrs.titleimagetop) {
              chartdata.title.style.marginTop = attrs.titleimagetop;
            }
            if (attrs.titleimageleft) {
              chartdata.title.style.marginLeft = attrs.titleimageleft;
            }
          }
          if (attrs.type === 'line') {
            chartsDefaults.chart.marginTop = 30;
            chartsDefaults.chart.spacingTop = 50;  //            chartsDefaults.chart.zoomType = null;
          }
          if (attrs.type === 'column') {
            chartsDefaults.chart.marginBottom = 80;  //            chartsDefaults.chart.spacingBottom = 50;
                                                     //            chartsDefaults.chart.zoomType = null;
          }
          if (attrs.type === 'area') {
            chartsDefaults.chart.spacingLeft = 0;
            chartsDefaults.chart.spacingRight = 0;
            chartsDefaults.chart.marginLeft = 0;
            chartsDefaults.chart.marginRight = 0;
          }
          Highcharts.setOptions({
            global: { useUTC: false },
            chart: { style: { fontFamily: 'Lato, Helvetica, Arial, sans-serif' } }
          });
          if (attrs.type === 'line') {
            var xAxis1 = chartdata.xAxis[0];
            //check for previous setting from service layer or json template... if it doesn't exist use the attr value
            if (!xAxis1.labels.formatter) {
              xAxis1.labels.formatter = new Function(attrs.xaxislabel);
            }
            if (!xAxis1.labels.step) {
              xAxis1.labels.step = attrs.xaxisstep;
            }  //end check
          }
          //pull any stringified from template JS and eval it
          if (chartdata.tooltip) {
            if (typeof chartdata.tooltip.formatter === 'string') {
              chartdata.tooltip.formatter = new Function(chartdata.tooltip.formatter);
            }  //            chartdata.tooltip.shared = true;
          }
          renderChart(chartsDefaults, chartdata);
        }
      });
    }
  };
});
function renderChart(chartsDefaults, chartdata, attrs) {
  var newSettings = {};
  $.extend(true, newSettings, chartsDefaults, chartdata);
  var chart = new Highcharts.Chart(newSettings);
}
onslyde.Services.factory('chartservice', function () {
  var areaChart, paretoChart, pieChart, pieCompare, xaxis, seriesIndex;
  return {
    convertLineChart: function (chartData, chartTemplate, dataDescription, settings) {
      var seriesCount = chartData.length, label;
      var lineChart = chartTemplate;
      seriesIndex = 0;
      lineChart.series = [];
      label = '';
      xaxis = lineChart.xAxis[0];
      //the next 2 setting options are provided in the timeFormat dropdown, so we must inspect them here
      if (settings.xaxisformat) {
        xaxis.labels.formatter = new Function(settings.xaxisformat);
      }
      if (settings.step) {
        xaxis.labels.step = settings.step;
      }
      //end check
      //check to see if there are multiple "chartGroupNames" in the object, otherwise "NA" will go to the else
      if (chartData.length > 1) {
        for (var l = 0; l < chartData.length; l++) {
          chartData.sort(function (a, b) {
            a = a['label'].toLowerCase();
            b = b['label'].toLowerCase();
            if (a === 'agree') {
              return -1;
            } else if (b === 'agree') {
              return 1;
            } else if (a === 'disagree') {
              return -1;
            } else if (b === 'disagree') {
              return 1;
            } else {
              return a > b ? 1 : a < b ? -1 : 0;
            }
          });
          var dataPoints = chartData[l].datapoints;
          lineChart.series[l] = {};
          lineChart.series[l].data = [];
          lineChart.series[l].name = chartData[l].label;
          lineChart.series[l].yAxis = 0;
          lineChart.series[l].type = 'line';
          lineChart.series[l].color = dataDescription.colors[l];
          lineChart.series[l].dashStyle = 'solid';
          //            lineChart.series[l].yAxis.title.text = dataDescription.yAxisLabels;
          for (var i = 0; i < dataPoints.length; i++) {
            if (typeof dataDescription.dataAttr[1] === 'object') {
              lineChart.series[l].data.push([
                dataPoints[i].timestamp,
                dataPoints[i].count
              ]);
            }
          }
        }
      }
      return lineChart;
    },
    convertAreaChart: function (chartData, chartTemplate, dataDescription, settings, currentCompare) {
      areaChart = angular.copy(areaChart);
      //      console.log('chartData',chartData[0])
      if (typeof chartData[0] === 'undefined') {
        chartData[0] = {};
        chartData[0].datapoints = [];
      }
      var dataPoints = chartData[0].datapoints, dPLength = dataPoints.length, label;
      if (currentCompare === 'YESTERDAY') {
        //        areaChart = chartTemplate;
        seriesIndex = dataDescription.dataAttr.length;
        label = 'Yesterday ';
      } else if (currentCompare === 'LAST_WEEK') {
        //        areaChart = chartTemplate;
        seriesIndex = dataDescription.dataAttr.length;
        label = 'Last Week ';
      } else {
        areaChart = chartTemplate;
        seriesIndex = 0;
        areaChart.series = [];
        label = '';
      }
      xaxis = areaChart.xAxis[0];
      xaxis.categories = [];
      //the next 2 setting options are provided in the timeFormat dropdown, so we must inspect them here
      if (settings.xaxisformat) {
        xaxis.labels.formatter = new Function(settings.xaxisformat);
      }
      if (settings.step) {
        xaxis.labels.step = settings.step;
      }
      //end check
      for (var i = 0; i < dPLength; i++) {
        var dp = dataPoints[i];
        xaxis.categories.push(dp.timestamp);
      }
      //check to see if there are multiple "chartGroupNames" in the object, otherwise "NA" will go to the else
      if (chartData.length > 1) {
        for (var l = 0; l < chartData.length; l++) {
          if (chartData[l].chartGroupName) {
            dataPoints = chartData[l].datapoints;
            //            dPLength = dataPoints.length;
            areaChart.series[l] = {};
            areaChart.series[l].data = [];
            areaChart.series[l].fillColor = dataDescription.areaColors[l];
            areaChart.series[l].name = chartData[l].chartGroupName;
            areaChart.series[l].yAxis = 0;
            areaChart.series[l].type = 'area';
            areaChart.series[l].pointInterval = 1;
            areaChart.series[l].color = dataDescription.colors[l];
            areaChart.series[l].dashStyle = 'solid';
            areaChart.series[l].yAxis.title.text = dataDescription.yAxisLabels;
            plotData(l, dPLength, dataPoints, dataDescription.detailDataAttr, true);
          }
        }
      } else {
        var steadyCounter = 0;
        //loop over incoming data members for axis setup... create empty arrays and settings ahead of time
        //the seriesIndex is for the upcoming compare options - if compare is clicked... if it isn't just use 0 :/
        for (var i = seriesIndex; i < dataDescription.dataAttr.length + (seriesIndex > 0 ? seriesIndex : 0); i++) {
          var yAxisIndex = dataDescription.multiAxis ? steadyCounter : 0;
          areaChart.series[i] = {};
          areaChart.series[i].data = [];
          areaChart.series[i].fillColor = dataDescription.areaColors[i];
          areaChart.series[i].name = label + dataDescription.labels[steadyCounter];
          areaChart.series[i].yAxis = yAxisIndex;
          areaChart.series[i].type = 'area';
          areaChart.series[i].pointInterval = 1;
          areaChart.series[i].color = dataDescription.colors[i];
          areaChart.series[i].dashStyle = 'solid';
          areaChart.yAxis[yAxisIndex].title.text = dataDescription.yAxisLabels[dataDescription.yAxisLabels > 1 ? steadyCounter : 0];
          steadyCounter++;
        }
        plotData(seriesIndex, dPLength, dataPoints, dataDescription.dataAttr, false);
      }
      function plotData(counter, dPLength, dataPoints, dataAttrs, detailedView) {
        //massage the data... happy ending
        for (var i = 0; i < dPLength; i++) {
          var dp = dataPoints[i];
          var localCounter = counter;
          //loop over incoming data members
          for (var j = 0; j < dataAttrs.length; j++) {
            if (typeof dp === 'undefined') {
              areaChart.series[localCounter].data.push(0);
            } else {
              areaChart.series[localCounter].data.push(dp[dataAttrs[j]]);
            }
            if (!detailedView) {
              localCounter++;
            }
          }
        }
      }
      return areaChart;
    },
    convertParetoChart: function (chartData, chartTemplate, dataDescription, settings, currentCompare) {
      if (typeof chartData === 'undefined') {
        chartData = [];
      }
      var label, cdLength = chartData.length, compare = false, allParetoOptions = [], stackedBar = false;
      paretoChart = chartTemplate;
      seriesIndex = 0;
      function getPreviousData() {
        for (var i = 0; i < chartTemplate.series[0].data.length; i++) {
          //pulling the "now" values for comparison later, assuming they will be in the 0 index :)
          allParetoOptions.push(chartTemplate.xAxis.categories[i]);
        }
      }
      if (typeof dataDescription.dataAttr[1] === 'object') {
        stackedBar = true;
      }
      if (currentCompare === 'YESTERDAY') {
        label = 'Yesterday ';
        compare = true;
        if (stackedBar) {
          seriesIndex = dataDescription.dataAttr[1].length;
        }
        getPreviousData();
      } else if (currentCompare === 'LAST_WEEK') {
        label = 'Last Week ';
        compare = true;
        if (stackedBar) {
          seriesIndex = dataDescription.dataAttr[1].length;
        }
        seriesIndex = getPreviousData();
      } else {
        compare = false;
        label = '';
        paretoChart.xAxis.categories = [];
        paretoChart.series = [];
        paretoChart.series[0] = {};
        paretoChart.series[0].data = [];
        paretoChart.legend.enabled = false;
      }
      paretoChart.plotOptions.series.borderColor = dataDescription.borderColor;
      //create a basic compare series (more advanced needed for stacked bar)
      if (compare && !stackedBar) {
        paretoChart.series[1] = {};
        paretoChart.series[1].data = [];
        //repopulate array with 0 values based on length of NOW data
        for (var i = 0; i < allParetoOptions.length; i++) {
          paretoChart.series[1].data.push(0);
        }
        paretoChart.legend.enabled = true;  //        paretoChart.series[1].name = label;
                                            //        paretoChart.series[0].name = "Now";
      }
      for (var i = 0; i < cdLength; i++) {
        var bar = chartData[i];
        if (!compare) {
          paretoChart.xAxis.categories.push(bar[dataDescription.dataAttr[0]]);
          //if we send multiple attributes to be plotted, assume it's a stacked bar for now
          if (typeof dataDescription.dataAttr[1] === 'object') {
            createStackedBar(dataDescription, paretoChart, paretoChart.series.length);
          } else {
            paretoChart.series[0].data.push(bar[dataDescription.dataAttr[1]]);
            paretoChart.series[0].name = dataDescription.labels[0];
            paretoChart.series[0].color = dataDescription.colors[0];
          }
        } else {
          //check if this is a stacked bar
          var newLabel = bar[dataDescription.dataAttr[0]], newValue = bar[dataDescription.dataAttr[1]], previousIndex = allParetoOptions.indexOf(newLabel);
          //make sure this label existed in the NOW data
          if (previousIndex > -1) {
            if (typeof dataDescription.dataAttr[1] === 'object') {
              createStackedBar(dataDescription, paretoChart, paretoChart.series.length);
            } else {
              paretoChart.series[1].data[previousIndex] = newValue;
              paretoChart.series[1].name = label !== '' ? label + ' ' + dataDescription.labels[0] : dataDescription.labels[0];
              paretoChart.series[1].color = dataDescription.colors[1];
            }
          } else {
          }
        }
      }
      function createStackedBar(dataDescription, paretoChart, startingPoint) {
        paretoChart.plotOptions = {
          series: {
            shadow: false,
            borderColor: dataDescription.borderColor,
            borderWidth: 1
          },
          column: {
            stacking: 'normal',
            dataLabels: {
              enabled: true,
              color: Highcharts.theme && Highcharts.theme.dataLabelsColor || 'white'
            }
          }
        };
        var start = dataDescription.dataAttr[1].length, steadyCounter = 0;
        stackName = label;
        if (compare) {
          paretoChart.legend.enabled = true;
        }
        for (var f = seriesIndex; f < start + seriesIndex; f++) {
          if (!paretoChart.series[f]) {
            paretoChart.series[f] = { 'data': [] };
          }
          paretoChart.series[f].data.push(bar[dataDescription.dataAttr[1][steadyCounter]]);
          paretoChart.series[f].name = label !== '' ? label + ' ' + dataDescription.labels[steadyCounter] : dataDescription.labels[steadyCounter];
          paretoChart.series[f].color = dataDescription.colors[f];
          paretoChart.series[f].stack = label;
          steadyCounter++;
        }
      }
      return paretoChart;
    },
    convertPieChart: function (chartData, chartTemplate, dataDescription, settings) {
      var label, cdLength = chartData.length, compare = false;
      pieChart = chartTemplate;
      compare = false;
      pieChart.series[0].data = [];
      if (pieChart.series[0].dataLabels) {
        if (typeof pieChart.series[0].dataLabels.formatter === 'string') {
          pieChart.series[0].dataLabels.formatter = new Function(pieChart.series[0].dataLabels.formatter);
        }
      }
      pieChart.plotOptions.pie.borderColor = dataDescription.borderColor;
      var tempArray = [];
      for (var i = 0; i < cdLength; i++) {
        var pie = chartData[i];
        tempArray.push({
          name: pie[dataDescription.dataAttr[0]],
          y: pie.datapoints[pie.datapoints.length - 1][dataDescription.dataAttr[1][1]],
          color: ''
        });
      }
      tempArray.sort(function (a, b) {
        a = a['name'].toLowerCase();
        b = b['name'].toLowerCase();
        return a > b ? 1 : a < b ? -1 : 0;
      });
      //add colors so they match up
      for (var i = 0; i < tempArray.length; i++) {
        tempArray[i].color = dataDescription.colors[i];
      }
      pieChart.series[0].data = tempArray;
      return pieChart;
    }
  };
});
//onslyde.Services.factory('$store', ['$parse', '$cookieStore', '$window', '$log', function ($parse, $cookieStore, $window, $log) {
angular.module('localStorage', ['ngCookies']).factory('$store', [
  '$parse',
  '$cookieStore',
  '$window',
  '$log',
  function ($parse, $cookieStore, $window, $log) {
    /**
   * Global Vars
   */
    var storage = typeof $window.localStorage === 'undefined' ? undefined : $window.localStorage;
    var supported = !(typeof storage === 'undefined' || typeof $window.JSON === 'undefined');
    var privateMethods = {
        parseValue: function (res) {
          var val;
          try {
            val = $window.JSON.parse(res);
            if (typeof val === 'undefined') {
              val = res;
            }
            if (val === 'true') {
              val = true;
            }
            if (val === 'false') {
              val = false;
            }
            if ($window.parseFloat(val) === val && !angular.isObject(val)) {
              val = $window.parseFloat(val);
            }
          } catch (e) {
            val = res;
          }
          return val;
        }
      };
    var publicMethods = {
        set: function (key, value) {
          if (!supported) {
            try {
              $cookieStore.put(key, value);
              return value;
            } catch (e) {
              $log.log('Local Storage not supported, make sure you have angular-cookies enabled.');
            }
          }
          var saver = $window.JSON.stringify(value);
          storage.setItem(key, saver);
          return privateMethods.parseValue(saver);
        },
        get: function (key) {
          if (!supported) {
            try {
              return privateMethods.parseValue($.cookie(key));
            } catch (e) {
              return null;
            }
          }
          var item = storage.getItem(key);
          return privateMethods.parseValue(item);
        },
        remove: function (key) {
          if (!supported) {
            try {
              $cookieStore.remove(key);
              return true;
            } catch (e) {
              return false;
            }
          }
          storage.removeItem(key);
          return true;
        },
        bind: function ($scope, key, opts) {
          var defaultOpts = {
              defaultValue: '',
              storeName: ''
            };
          // Backwards compatibility with old defaultValue string
          if (angular.isString(opts)) {
            opts = angular.extend({}, defaultOpts, { defaultValue: opts });
          } else {
            // If no defined options we use defaults otherwise extend defaults
            opts = angular.isUndefined(opts) ? defaultOpts : angular.extend(defaultOpts, opts);
          }
          // Set the storeName key for the localStorage entry
          // use user defined in specified
          var storeName = opts.storeName || key;
          // If a value doesn't already exist store it as is
          if (!publicMethods.get(storeName)) {
            publicMethods.set(storeName, opts.defaultValue);
          }
          // If it does exist assign it to the $scope value
          $parse(key).assign($scope, publicMethods.get(storeName));
          // Register a listener for changes on the $scope value
          // to update the localStorage value
          $scope.$watch(key, function (val) {
            if (angular.isDefined(val)) {
              publicMethods.set(storeName, val);
            }
          }, true);
          return publicMethods.get(storeName);
        },
        unbind: function ($scope, key, storeName) {
          storeName = storeName || key;
          $parse(key).assign($scope, null);
          $scope.$watch(key, function () {
          });
          publicMethods.remove(storeName);
        },
        clearAll: function () {
          storage.clear();
        }
      };
    return publicMethods;
  }
]);
'use strict';
onslyde.Controllers.controller('LoginCtrl', [
  '$store',
  '$http',
  '$scope',
  '$rootScope',
  '$routeParams',
  '$location',
  function ($store, $http, $scope, $rootScope, $routeParams, $location) {
    $store.bind($rootScope, 'userInfo');
    $scope.loginButton = {
      label: 'Sign In',
      disabled: false
    };
    $scope.login = function (email, password) {
      //    if($scope.signin.$valid){
      $scope.loginButton.label = 'Signing in...';
      $scope.loginButton.disabled = true;
      var email = email || $scope.login.email;
      var password = password || $scope.login.password;
      $http({
        method: 'POST',
        url: $rootScope.urls() + '/go/members/login',
        data: $.param({
          email: email,
          password: password
        }),
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      }).success(function (data, status, headers, config) {
        if (data.created) {
          $('#signin').foundation('reveal', 'close');
          $rootScope.userInfo = data;
          $store.set('userInfo', $rootScope.userInfo);
          $store.bind($rootScope, 'userInfo');
        } else {
          $scope.loginAlert = 'There was a problem logging in: ' + data.name;
        }
        $scope.loginButton.label = 'Sign In';
        $scope.loginButton.disabled = false;
      }).error(function (data, status, headers, config) {
        $scope.loginAlert = 'Wrong password or user not found.';
        $scope.loginButton.label = 'Sign In';
        $scope.loginButton.disabled = false;
      });  //    }
           //    $scope.signin.$setPristine();
    };
    $scope.register = {};
    $scope.registerMessage = '';
    $scope.signupButton = {
      label: 'Create',
      disabled: false
    };
    $scope.registerUser = function () {
      if ($scope.signup.$valid) {
        $scope.signupButton.disabled = true;
        $scope.signupButton.label = 'Creating...';
        var email = $scope.register.email;
        var password = $scope.register.password;
        var name = $scope.register.fullName;
        $http({
          method: 'POST',
          url: $rootScope.urls() + '/go/members',
          data: $scope.register,
          headers: {}
        }).success(function (data, status, headers, config) {
          $rootScope.registerMessage = data;
          $store.set('registerMessage', $rootScope.registerMessage);
          $scope.login(email, password);
          $location.path('/gettingstarted');
          $scope.signupButton.disabled = false;
          $scope.signupButton.label = 'Create';
        }).error(function (data, status, headers, config) {
          $scope.registerAlert = 'Problem registering user: ' + data.email;
          $scope.signupButton.disabled = false;
          $scope.signupButton.label = 'Create';
        });
      }
    };
    $scope.logout = function () {
      $store.remove('userInfo');
      $store.remove('registerMessage');
      $rootScope.userInfo = {};
      $rootScope.registerMessage = {};
    };
    $rootScope.$on('userNotAuthenticated', function (event) {
      $location.path('/login');
    });
    $scope.$on('loginSuccesful', function (event, user, organizations, applications) {
    });
  }
]);
'use strict';
onslyde.Controllers.controller('PageCtrl', [
  'pagedata',
  'utility',
  '$scope',
  '$rootScope',
  '$location',
  '$routeParams',
  '$q',
  '$route',
  '$store',
  function (pagedata, utility, $scope, $rootScope, $location, $routeParams, $q, $route, $store) {
    $scope.location = $location;
    $rootScope.urls = function () {
      var BASE_URL = '';
      if ($location.host() === 'onslyde.com' || $location.host() === 'www.onslyde.com') {
        BASE_URL = 'https://www.onslyde.com:8443';
      } else {
        BASE_URL = 'https://localhost:8443';
      }
      return BASE_URL;
    };
    //    $rootScope.userInfo = {};
    //todo - use $rootScope.registerMessage to see if there is an unfinished pres.
    if (!$rootScope.registerMessage && $store.get('registerMessage')) {
      $store.bind($rootScope, 'registerMessage');
    }
    if (!$rootScope.userInfo && $store.get('userInfo')) {
      $store.bind($rootScope, 'userInfo');
    }  //    $scope.$on('$routeChangeSuccess', function () {
       //
       //
       //
       //    });
  }
]);
onslyde.Services.factory('pagedata', function ($q, $http, $resource, $rootScope) {
  return {
    get: function (id, url) {
      var items, deferred;
      deferred = $q.defer();
      $http.get(url || configuration.ITEMS_URL).success(function (data, status, headers, config) {
        var result;
        if (id) {
          angular.forEach(data, function (obj, index) {
            if (obj.id === id) {
              result = obj;
            }
          });
        } else {
          result = data;
        }
        deferred.resolve(result);
      }).error(function (data, status, headers, config) {
        console.error(data, status, headers, config);
        deferred.reject(data);
      });
      return deferred.promise;
    },
    getLocal: function (url) {
      var retdata;
      $http.get(url).success(function (data, status, headers, config) {
        retdata = data;
      }).error(function (data, status, headers, config) {
        console.error(data, status, headers, config);
      });
      return retdata;
    },
    jsonp: function (objectType, criteriaId, params, successCallback) {
      if (!params) {
        params = {};
      }
      params.demoApp = $rootScope.demoData ? true : false;
      params.access_token = localStorage.getItem('accessToken');
      params.callback = 'JSON_CALLBACK';
      $http.jsonp($rootScope.urls().DATA_URL + '/' + $rootScope.currentOrg + '/' + $rootScope.currentApp + '/apm/' + objectType + '/' + criteriaId, {
        params: params,
        headers: {}
      }).success(function (data, status, headers, config) {
        successCallback(data, status, headers, config);
      }).error(function (data, status, headers, config) {
        console.log('ERROR: Could not get jsonp data. ' + $rootScope.urls().DATA_URL + objectType + '/' + criteriaId);
      });
    },
    jsonp_simple: function (objectType, appId, params) {
      if (!params) {
        params = {};
      }
      params.access_token = localStorage.getItem('accessToken');
      params.callback = 'JSON_CALLBACK';
      var deferred = $q.defer();
      $http.jsonp($rootScope.urls().DATA_URL + '/' + $rootScope.currentOrg + '/' + $rootScope.currentApp + '/apm/' + objectType + '/' + appId, { params: params }).success(function (data, status, headers, config) {
        deferred.resolve(data);
      }).error(function (data, status, headers, config) {
        console.log('ERROR: Could not get jsonp data. ' + $rootScope.urls().DATA_URL + '/' + $rootScope.currentOrg + '/' + $rootScope.currentApp + '/apm/' + objectType + '/' + appId);
        deferred.reject(data);
      });
      return deferred.promise;
    },
    jsonp_raw: function (objectType, appId, params) {
      if (!params) {
        params = {};
      }
      params.access_token = localStorage.getItem('accessToken');
      params.callback = 'JSON_CALLBACK';
      var deferred = $q.defer();
      $http.jsonp($rootScope.urls().DATA_URL + '/' + $rootScope.currentOrg + '/' + $rootScope.currentApp + '/' + objectType, { params: params }).success(function (data, status, headers, config) {
        deferred.resolve(data);
      }).error(function (data, status, headers, config) {
        console.log('ERROR: Could not get jsonp data. ' + $rootScope.urls().DATA_URL + objectType + '/' + appId);
        deferred.reject(data);
      });
      return deferred.promise;
    },
    resource: function (params, isArray) {
      //temporary url for REST endpoints
      return $resource($rootScope.urls().DATA_URL + '/:orgname/:appname/:username/:endpoint', {}, {
        get: {
          method: 'JSONP',
          isArray: isArray,
          params: params
        },
        login: {
          method: 'GET',
          url: $rootScope.urls().DATA_URL + '/management/token',
          isArray: false,
          params: params
        },
        save: {
          url: $rootScope.urls().DATA_URL + '/' + params.orgname + '/' + params.appname,
          method: 'PUT',
          isArray: false,
          params: params
        }
      });
    }
  };
});
onslyde.Services.factory('utility', function ($q, $http, $resource) {
  return {
    get_qs_params: function () {
      var queryParams = {};
      if (window.location.search) {
        // split up the query string and store in an associative array
        var params = window.location.search.slice(1).split('&');
        for (var i = 0; i < params.length; i++) {
          var tmp = params[i].split('=');
          queryParams[tmp[0]] = unescape(tmp[1]);
        }
      }
      return queryParams;
    },
    safeApply: function (fn) {
      var phase = this.$root.$$phase;
      if (phase == '$apply' || phase == '$digest') {
        if (fn && typeof fn === 'function') {
          fn();
        }
      } else {
        this.$apply(fn);
      }
    }
  };
});
onslyde.Directives.directive('youtube', [
  'youtubeapi',
  function (youtubeapi) {
    return {
      restrict: 'A',
      link: function (scope, element, attrs) {
        youtubeapi.videoId = attrs.sessionid;
        youtubeapi.bindVideoPlayer(element[0].id);
        youtubeapi.loadPlayer();
      }
    };
  }
]);
onslyde.Services.factory('youtubeapi', function ($window, $rootScope, $log) {
  var service = $rootScope;
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
    //    $log.info('Binding to player ' + elementId);
    service.playerId = elementId;
  };
  service.createPlayer = function () {
    //    $log.info('Creating a new Youtube player for DOM id ' + this.playerId + ' and video ' + this.videoId);
    return new YT.Player(this.playerId, {
      height: this.playerHeight,
      width: this.playerWidth,
      videoId: this.videoId,
      events: { 'onReady': $window.onPlayerReady }
    });
  };
  service.loadPlayer = function () {
    // API ready?
    if (this.ready && this.playerId && this.videoId) {
      if (this.player) {
        this.player.destroy();
      }
      this.player = this.createPlayer();  // fixed Unable to post message to https://www.youtube.com.
                                          // ref: https://code.google.com/p/gdata-issues/issues/detail?id=4697
                                          //      setTimeout(function(){
                                          //        var url = $('#analytics-player').prop('src');
                                          //        if (url.match('^http://')) {
                                          //          $('#analytics-player').prop('src', url.replace(/^http:\/\//i, 'https://'));
                                          //        }
                                          //      }, 500);
    }
  };
  return service;
});
'use strict';
onslyde.Controllers.controller('GetStartedCtrl', [
  '$scope',
  '$rootScope',
  '$location',
  '$http',
  '$store',
  function ($scope, $rootScope, $location, $http, $store) {
    $scope.getstarted = {};
    $scope.viewPres = function (demo) {
      var sessionID;
      if ($rootScope.registerMessage && $rootScope.registerMessage.sessionId) {
        sessionID = $rootScope.registerMessage.sessionId;
      }
      if (demo) {
        $rootScope.userInfo = {};
        $rootScope.userInfo.email = 'someemail@nowhere.com';
        sessionID = 201;
        $scope.getstarted.twitter = 'wesleyhales';
        $scope.getstarted.presName = 'My awesome presentation';
        $scope.getstarted.poll1 = 'Do you like my party hat?';
        $scope.getstarted.option1 = 'Yep';
        $scope.getstarted.option2 = 'Nope!';
      }
      window.open($rootScope.urls() + '/go/template/download?' + 'deckType=' + $scope.getstarted.deck + '&' + 'email=' + $rootScope.userInfo.email + '&' + 'sessionId=' + (sessionID || '') + '&' + 'token=' + $rootScope.userInfo.created + '&' + 'twitter=' + $scope.getstarted.twitter + '&' + 'presName=' + $scope.getstarted.presName + '&' + 'poll1=' + $scope.getstarted.poll1 + '&' + 'option1=' + $scope.getstarted.option1 + '&' + 'option2=' + $scope.getstarted.option2);
      //need a better check to make sure pres was created, or show it as created in ui
      $store.remove('registerMessage');
    };
    $scope.setupPres = function () {
      $scope.getstarted.email = $rootScope.userInfo.email;
      //if we send empty, it will create new
      $scope.getstarted.sessionId = $rootScope.registerMessage && $rootScope.registerMessage.sessionId || '';
      $scope.getstarted.token = $rootScope.userInfo.created;
      $http({
        method: 'POST',
        url: $rootScope.urls() + '/go/template/create',
        data: $scope.getstarted,
        headers: {}
      }).success(function (data, status, headers, config) {
        //          console.log(data)
        var frame = getFrame();
        frame.write(data);
        frame.location = '#';
      }).error(function (data, status, headers, config) {
        console.log('error', data);
      });
      //need a better check to make sure pres was created, or show it as created in ui
      $store.remove('registerMessage');
      var getFrame = function () {
        var frame = document.getElementById('temp-frame');
        if (!frame) {
          // create frame
          frame = document.createElement('iframe');
          frame.setAttribute('id', 'temp-frame');
          frame.setAttribute('name', 'temp-frame');
          frame.setAttribute('seamless', '');
          frame.setAttribute('sandbox', 'allow-same-origin allow-scripts allow-popups allow-forms');
          frame.setAttribute('height', '768px');
          frame.setAttribute('width', '100%');
          frame.setAttribute('allowFullScreen', '');
          //          frame.setAttribute("onload","setTimeout(function(){window.slidfast({onslyde: {deck:true,sessionID: 224, mode:'reveal'}})},1000)");
          document.getElementById('preview-container').appendChild(frame);
        }
        // load a page
        return frame.contentDocument;
      };
    };
    $scope.downloadPres = function () {
      $scope.getstarted.email = $rootScope.userInfo.email;
      //if we send empty, it will create new
      $scope.getstarted.sessionId = $rootScope.registerMessage && $rootScope.registerMessage.sessionId || '';
      $scope.getstarted.token = $rootScope.userInfo.created;
      $http({
        method: 'GET',
        url: $rootScope.urls() + '/go/template/download',
        data: $scope.getstarted,
        headers: {}
      }).success(function (data, status, headers, config) {
        console.log(data);
      }).error(function (data, status, headers, config) {
        console.log('error', data);
      });
      //need a better check to make sure pres was created, or show it as created in ui
      $store.remove('registerMessage');
    };
  }
]);