'use strict'

onslyde.Controllers.controller('AnalyticsCtrl',
  ['pagedata',
    'chartservice',
    '$scope',
    '$rootScope',
    '$routeParams',
    '$timeout', 'youtubeapi', '$location', '$anchorScroll', function (pagedata, chartservice, $scope, $rootScope, $routeParams, $timeout, youtubeapi, $location, $anchorScroll) {

    $scope.sessionID = $routeParams.sessionID;

    $scope.presAnalyticsSetup = function(){
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
          yAxisLabels: [''],
          labels:['Total'],
          dataAttr:['label', ['timestamp','count']],
          colors:['rgba(43,166,203,0.9)',
                  'rgba(52,52,52,0.3)',
            'rgba(169,234,181,0.9)',
            'rgba(43,43,43,0.5)'],
          borderColor:'#1b97d1'
        },
        pie: {
          dataAttr: ['label', ['timestamp','count']],
          colors: [
            'rgba(43,166,203,0.4)',
            'rgba(52,52,52,0.3)',
            'rgba(169,234,181,0.5)',
            'rgba(43,43,43,0.5)',
            '#ff9191', '#ffa1a1', '#ffb6b6', '#ffcbcb'],
          borderColor: '#ff0303'
        }
      };



      $scope.createCharts = function(){
          pagedata.get(null, $rootScope.urls() + '/go/analytics/' + $routeParams.sessionID).then(function (success) {
            $rootScope.sessionData = success;


            $scope.twoOptionsList = [];

            $scope.dashBoard = {};
            $scope.dashBoard.totals = {agree: 1, disagree: 1};
            $scope.dashBoard.speakerTotals = [];
            $scope.dashBoard.sessionVotesFilterList =
              [
                {value: 1},
                {value: 2},
                {value: 3},
                {value: 4},
                {value: 5},
                {value: 6},
                {value: 7},
                {value: 8},
                {value: 9},
                {value: 10}
              ];

            $scope.dashBoard.sessionVotesFilter = $scope.dashBoard.sessionVotesFilterList[1];

            $scope.$watch('dashBoard.sessionVotesFilter',function(newVal,oldVal){
              if(newVal !== oldVal){
                $scope.twoOptionsList = [];
                $scope.dashBoard.totals = {agree: 1, disagree: 1};
                $scope.dashBoard.speakerTotals = [];
                createCharts();
              }
            });


            createCharts();

            function createCharts(){

            angular.forEach($scope.sessionData.slideGroups, function(value, index){

              var twooptions = [],
                voteData,
                voteOptions,
                allVotes = [],
                chartPlotObject = {label:'',datapoints:[]};

              if(value.slideGroupOptionses.length > 2){

                twooptions.push(chartPlotObject);
                twooptions.push(chartPlotObject);
                twooptions.push(chartPlotObject);
                twooptions.push(chartPlotObject);

                allVotes.push(chartPlotObject);
                allVotes.push(chartPlotObject);
                allVotes.push(chartPlotObject);
                allVotes.push(chartPlotObject);

                voteData = value.slideGroupVoteses;
                voteOptions = value.slideGroupOptionses;
                twooptions.topicName = value.groupName;

                try {
                  twooptions.topicImage = value.slides[0].screenshot;
                } catch (e) {
                }
              }else{

                twooptions.push(chartPlotObject);
                twooptions.push(chartPlotObject);

                allVotes.push(chartPlotObject);
                allVotes.push(chartPlotObject);

                if(typeof value.slides[0] !== 'undefined'){
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

              twooptions.speakerData = $scope.getPanelist($routeParams.sessionID,twooptions.topicName);

              //if we have atleast 1 vote on the topic
              if(voteData){
                if(voteData.length >= $scope.dashBoard.sessionVotesFilter.value){

                // summary data for speaker display at top
                var spearkerStat = getSpeakerSummaryData($scope.dashBoard.speakerTotals,twooptions);

                var optionTracker = {},
                  slideOptions = voteOptions;

                //get list of available options
                for(var i=0;i<slideOptions.length;i++){
                  twooptions[i].label = slideOptions[i].name;
                  allVotes[i].label = slideOptions[i].name;
                  optionTracker[slideOptions[i].name] = 0;
                  twooptions[i].datapoints.push({"timestamp":0,"count":0});
                }


                //get the votes and attendee data for agree/disagree ONLY options
                angular.forEach(voteData, function(vote, toindex){
                  var voteTime = vote.voteTime,
                    attendee = vote.attendee,
                    thisSlideOptions;

                  if(twooptions.length === 2){
                    thisSlideOptions = vote.slideOptions;
                  }else{
                    thisSlideOptions = vote.slideGroupOptions;
                  }

                  //loop through all options for compare... this is so we can display a growth chart and not just spikes when votes occur
                  for(var i=0;i<twooptions.length;i++){
                    var
                      dataPoints = twooptions[i].datapoints,
                      totalLength = dataPoints[dataPoints.length-1],
                      lastValue;


                     //if we find a vote, fill it in to the approriate array
                    //todo - cleanup repetitive code
                    if(twooptions[i].label === thisSlideOptions.name){

                      if(optionTracker[twooptions[i].label] === 0  && totalLength.timestamp === 0){
                        twooptions[i].datapoints[0] = {"timestamp":voteTime,"count":1};
                        optionTracker[twooptions[i].label] = 1;
                      }else{
                        if(optionTracker[twooptions[i].label] === 0){
                          optionTracker[twooptions[i].label] = 1;
                        }
                        //increment the option tracker for this label by 1
                        twooptions[i].datapoints.push({"timestamp":voteTime,"count":optionTracker[twooptions[i].label]++});
                      }
                      //increment total count for summary
                      $scope.dashBoard.totals[twooptions[i].label] += 1;

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


                //increment total count for speaker
                for (var i = 0; i < twooptions.length; i++) {
                  spearkerStat[twooptions[i].label] += twooptions[i].datapoints[twooptions[i].datapoints.length-1].count;
                }
                spearkerStat.sessions += 1;
                $scope.dashBoard.speakerTotals.push(spearkerStat);


                var startTime = $rootScope.sessionData.end;
                var tempLineChart = angular.copy($rootScope.chartTemplate.line);
                var tempPieChart = angular.copy($rootScope.chartTemplate.pie);

                twooptions.chartData = chartservice.convertLineChart(twooptions, tempLineChart, dataDescription.timeseries, '');
                twooptions.pieChartData = chartservice.convertPieChart(twooptions, tempPieChart, dataDescription.pie, '');


                var createYoutubeClickable = function(series) {

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

                if($rootScope.sessionData.sessionCode && $rootScope.sessionData.sessionCode !== 'beta'){
                  createYoutubeClickable(twooptions.chartData.series[0]);
                  createYoutubeClickable(twooptions.chartData.series[1]);
                }
                $scope.twoOptionsList.push(twooptions);



              }
              }
            });


            }

            //todo change timeout to after angular document loaded
            if($location.hash()){
              $timeout(function(){$anchorScroll($location.hash())},2000);
            }


          }, function (fail) {
            console.log('Problem getting chart datapoints', fail)
          });

        };

      function getSpeakerSummaryData(speakerTotals,twooptions){
        var spearkerStat = {topic:twooptions.topicName,agree:0,disagree:0,sessions:0,speaker:twooptions.speakerData};

        if(speakerTotals.length > 0){
          for(var d=0;d<speakerTotals.length;d++){
            if(speakerTotals[d].topic === twooptions.topicName){
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
      }else{
        $scope.createCharts();
      }

      //todo - this is a temporary lookup for edge. need to add columns to attendee db for pic and twitter
      $scope.getPanelist = function(sessionID, name) {
        var result;

        var allpanels =
        [
            {name:'Natasha Rooney', org:'GSMA', pic:'http://edgeconf.com/2013-nyc/images/panelists/natasha-rooney.jpg', twitter:'thisnatasha', mod:true},
            {name:'Manu Sporny', org:'Digital Bazaar', pic:'http://edgeconf.com/2013-nyc/images/panelists/manu-sporny.jpg', twitter:'manusporny'},
            {name:'Rob Grimshaw', org:'FT.com', pic:'http://edgeconf.com/2013-nyc/images/panelists/rob-grimshaw.jpg', twitter:'r_g'},
            {name:'Cyndy Lobb', org:'Google', pic:'http://edgeconf.com/2013-nyc/images/panelists/cyndy-lobb.jpg', twitter:''},
            {name:'Ricardo Varela', org:'Telefónica', pic:'http://edgeconf.com/2013-nyc/images/panelists/ricardo-varela.jpg', twitter:'phobeo'},
            {name:'Kumar McMillan', org:'Mozilla', pic:'http://edgeconf.com/2013-nyc/images/panelists/kumar-mcmillan.jpg', twitter:'kumar303'},

            {name:'Marcos Caceres', org:'Mozilla', pic:'http://edgeconf.com/2013-nyc/images/panelists/marcos-caceres.jpg', twitter:'marcosc', mod:true},
            {name:'Yoav Weiss', org:'WL Square', pic:'http://edgeconf.com/2013-nyc/images/panelists/yoav-weiss.jpg', twitter:'yoavweiss'},
            {name:'Ann Robson', org:'freelance', pic:'http://edgeconf.com/2013-nyc/images/panelists/ann-robson.jpg', twitter:'arobson'},
            {name:'Estelle Weyl', org:'freelance', pic:'http://edgeconf.com/2013-nyc/images/panelists/estelle-weyl.jpg', twitter:'estellevw'},
            {name:'Peter Miller', org:'Conde Nast', pic:'http://edgeconf.com/2013-nyc/images/panelists/peter-miller.jpg', twitter:'petemill'},
            {name:'John Mellor', org:'Google', pic:'http://edgeconf.com/2013-nyc/images/panelists/john-mellor.jpg', twitter:''},

            {name:'Andre Behrens', org:'The New York Times', pic:'http://edgeconf.com/2013-nyc/images/panelists/andre-behrens.jpg', twitter:'mrandre', mod:true},
            {name:'Jonathan Klein', org:'Etsy', pic:'http://edgeconf.com/2013-nyc/images/panelists/jonathan-klein.jpg', twitter:'jonathanklein'},
            {name:'Paul Lewis', org:'Google', pic:'http://edgeconf.com/2013-nyc/images/panelists/paul-lewis.jpg', twitter:'aerotwist'},
            {name:'Ariya Hidayat', org:'Sencha', pic:'http://edgeconf.com/2013-nyc/images/panelists/ariya-hidayat.jpg', twitter:'ariyahidayat'},
            {name:'Joshua Peek', org:'Github', pic:'http://edgeconf.com/2013-nyc/images/panelists/joshua-peek.jpg', twitter:'joshpeek'},
            {name:'Eli Fidler', org:'BlackBerry', pic:'http://edgeconf.com/2013-nyc/images/panelists/eli-fidler.jpg', twitter:'efidler'},

            {name:'Scott Jenson', org:'Jenson Design', pic:'http://edgeconf.com/2013-nyc/images/panelists/scott-jenson.jpg', twitter:'scottjenson', mod:true},
            {name:'Martyn Loughran', org:'Pusher', pic:'http://edgeconf.com/2013-nyc/images/panelists/martyn-loughran.jpg', twitter:'mloughran'},
            {name:'Wesley Hales', org:'Apigee', pic:'http://edgeconf.com/2013-nyc/images/panelists/wesley-hales.jpg', twitter:'wesleyhales'},
            {name:'Rob Hawkes', org:'freelance', pic:'http://edgeconf.com/2013-nyc/images/panelists/rob-hawkes.jpg', twitter:'robhawkes'},
            {name:'John Fallows', org:'Kaazing', pic:'http://edgeconf.com/2013-nyc/images/panelists/john-fallows.jpg', twitter:''},
            {name:'Henrik Joretag', org:'&yet', pic:'http://edgeconf.com/2013-nyc/images/panelists/henrik-joretag.jpg', twitter:'HenrikJoreteg'},

            {name:'Paul Irish', org:'Google', pic:'http://edgeconf.com/2013-nyc/images/panelists/paul-irish.jpg', twitter:'paul_irish', mod:true},
            {name:'Tom Maslen', org:'BBC', pic:'http://edgeconf.com/2013-nyc/images/panelists/tom-maslen.jpg', twitter:'tmaslen'},
            {name:'Tomomi Imura', org:'Nokia', pic:'http://edgeconf.com/2013-nyc/images/panelists/tomomi-imura.jpg', twitter:'girlie_mac'},
            {name:'Shwetank Dixit', org:'Opera', pic:'http://edgeconf.com/2013-nyc/images/panelists/shwetank-dixit.jpg', twitter:'shwetank'},
            {name:'Edd Sowden', org:'Government Digital Service', pic:'http://edgeconf.com/2013-nyc/images/panelists/edd-sowden.jpg', twitter:'edds'},

            {name:'Steve Thair', org:'Seriti Consulting', pic:'http://edgeconf.com/2013-nyc/images/panelists/steve-thair.jpg', twitter:'theopsmgr', mod:true},
            {name:'Ben Vinegar', org:'Disqus', pic:'http://edgeconf.com/2013-nyc/images/panelists/ben-vinegar.jpg', twitter:'bentlegen'},
            {name:'Guy Podjarny', org:'Akamai', pic:'http://edgeconf.com/2013-nyc/images/panelists/guy-podjarny.jpg', twitter:'guypod'},
            {name:'Stoyan Stefanov', org:'Facebook', pic:'http://edgeconf.com/2013-nyc/images/panelists/stoyan-stefanov.jpg', twitter:'stoyanstefanov'},
            {name:'Barbara Bermes', org:'CBC', pic:'http://edgeconf.com/2013-nyc/images/panelists/barbara-bermes.jpg', twitter:'bbinto'},

            {name:'Jake Archibald', org:'Google', pic:'http://edgeconf.com/2013-nyc/images/panelists/jake-archibald.jpg', twitter:'jaffathecake', mod:true},
            {name:'Alex Russell', org:'Google', pic:'http://edgeconf.com/2013-nyc/images/panelists/alex-russell.jpg', twitter:'slightlylate'},
            {name:'Matt Andrews', org:'FT Labs', pic:'http://edgeconf.com/2013-nyc/images/panelists/matt-andrews.jpg', twitter:'andrewsmatt'},
            {name:'Craig Cavalier', org:'LiquidFrameworks', pic:'http://edgeconf.com/2013-nyc/images/panelists/craig-cavalier.jpg', twitter:'CraigCav'},
            {name:'Calvin Spealman', org:'Caktus Consulting', pic:'http://edgeconf.com/2013-nyc/images/panelists/calvin-spealman.jpg', twitter:'ironfroggy'}
          ];





        angular.forEach(allpanels, function(value, index){
          if(value.name === name){
            result = value;
          }
        });

        return result;
      };

    };


  }]);