(function(window){

var min = 255;
var max = 999;

//emulate remote control ip generation
var createRandom = function () {
  function base(){
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }
  return base() + '.' + base() + '.' + base() + '.' + base();
};

  var conn1 = {}, conn1thread, counter = 0;

  for(var i=0;i<5;i++){


    //setInterval(function(){

      conn1[counter] = new WebSocket('wss://www.onslyde.com/ws/?session=640&attendeeIP=' + createRandom());

     // conn1[counter].addEventListener('open', function(e){
        //have this connection send out votes randomly every x minutes/seconds
        //this.send('props:' + (Math.floor(Math.random()*2) === 0 ? 'agree':'disagree') +',,,' + new Date().getTime());


       // conn1[counter].close();
      //});



    //},1500);

    counter++;
  }

})(window);