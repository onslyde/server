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

  for(var i=0;i<50;i++){

    conn1[counter] = new WebSocket('wss://www.onslyde.com/ws/?session=619&attendeeIP=' + createRandom());

    conn1[counter].addEventListener('open', function(e){
      //send 1 vote on open
      this.send('vote:' + (Math.floor(Math.random()*2) === 0 ? 'option1':'option2') +',,,' + new Date().getTime());
      startSession(this);
      //this.close();
    });

    counter++;
  }

  //simulate each connection sending random bursts of data
  function startSession(conn){
    setInterval(function(){
      console.log(conn);
      conn.send('vote:' + (Math.floor(Math.random()*2) === 0 ? 'option1':'option2') +',,,' + new Date().getTime());
    },(Math.floor(Math.random()*2) === 0 ? 15000:10000)); //randomly submit vote at 10 or 15 seconds
  }

})(window);