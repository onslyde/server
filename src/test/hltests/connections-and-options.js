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

  var conn1 = {}, conn1thread;
  for(var i=0;i<20;i++){

    var index = i;

    conn1[index] = new WebSocket('wss://www.onslyde.com/ws/?session=167&attendeeIP=' + createRandom());

    conn1[index].addEventListener('open', function(e){
      //have this connection send out votes randomly for x minutes/seconds
      this.send('props:agree,,,' + new Date().getTime());
      this.send('props:disagree,,,' + new Date().getTime());
    });

  }

})(window);