var foo = {};

function connect(i){
  var index = i;
  foo[i] = new WebSocket('wss://www.onslyde.com/ws/?session=1' + i + '&attendeeIP=910.377.611.322');
  console.log(i,foo[i].readyState);
  foo[i + '_interval'] = setInterval(function(){
    console.log('-',i,foo[i]);
    if(foo[i].readyState === 1){
      foo[i].send('::connect::');
      foo[i].send('activeOptions:test1' + i + ',test2' + i + ',1:0');
      clearInterval(foo[i + '_interval']);
      if(i < 44){
        i++;
        connect(i)
      }

    }
  },1000)
}

connect(33);


var bar = {};

function send(i){
  var index = i;
  bar[i] = new WebSocket('wss://www.onslyde.com/ws/?session=1' + i + '&attendeeIP=910.377.611.3' + i);
  bar[i + '_interval'] = setInterval(function(){
    console.log('-send',i,bar[i]);
    if(bar[i].readyState === 1){
      for(var j=0;j<10;j++){
        bar[i].send('vote:test1' + i + ',0,0,0');
      }
      clearInterval(bar[i + '_interval']);
      if(i < 44){
        i++;
        send(i)
      }

    }
  },1000)
}

setTimeout(function() {
  send(33);
},10000);

