var speak = document.querySelector('#speak');
var wtf = document.querySelector('#wtf');
var nice = document.querySelector('#nice');
var voteLabel = document.querySelector('#vote-label');
var voted;
//todo make this unique user for session management/voter registration
//var ws = slidfast.ws.join('client:anonymous2');

disablePoll();

speak.onclick = function(event) {
  _gaq.push(['_trackEvent', 'onslyde-option1', 'vote']);
  ws.send('speak:' + JSON.stringify(userObject));

};

var niceTimeout,
  wtfTimeout;

wtf.onclick = function(event) {
  _gaq.push(['_trackEvent', 'onslyde-wtf', 'vote']);
  ws.send('vote:wtf');
  wtf.disabled = true;
  wtf.style.opacity = .4;
  wtf.value = "vote again in 30 seconds";
  clearTimeout(wtfTimeout);
  wtfTimeout = setTimeout(function(){
    wtf.disabled = false;
    wtf.style.opacity = 1;
    wtf.value = 'Thumbs down!'
  },30000);
  return false;
};

nice.onclick = function(event) {
  _gaq.push(['_trackEvent', 'onslyde-nice', 'vote']);
  ws.send('vote:nice');
  nice.disabled = true;
  nice.style.opacity = .4;
  nice.value = "vote again in 30 seconds";
  clearTimeout(niceTimeout);
  niceTimeout = setTimeout(function(){
    nice.disabled = false;
    nice.style.opacity = 1;
    nice.value = 'Thumbs up!'
  },30000);
  return false;
};

function disablePoll(){
  wtf.disabled = true;
  wtf.disabled = true;
  nice.style.opacity = .4;
  nice.style.opacity = .4;

  speak.disabled = true;
  speak.style.opacity = .4;
  voteLabel.innerHTML = 'Waiting...';
}

function enablePoll(){
  speak.disabled = false;
  wtf.disabled = false;
  nice.disabled = false;
  wtf.value = 'Thumbs Down!';
  nice.value = 'Nice!';
  speak.style.opacity = 1;
  wtf.style.opacity = 1;
  nice.style.opacity = 1;
  voteLabel.innerHTML = 'Vote!';
  voted = false;
}

window.addEventListener('updateOptions', function(e) {
  console.log('updateOptions',e);
  enablePoll();
}, false);

//callback for pressing the speak button (managed server side)
window.addEventListener('speak', function(e) {
  if(e.position === '777'){
    speak.value = 'Thanks for speaking!';
  }else{
    speak.value = 'You are queued to speak';
  }

}, false);

window.addEventListener('remoteMarkup', function(e) {
//  console.log('e', typeof e.markup);

  if(e.markup !== ''){
    var markup = jQuery.parseJSON(e.markup);
    try {
      document.getElementById('from-slide').innerHTML = decodeURIComponent(markup.remoteMarkup);
    } catch (e) {
    }
  }

  //checking for type of object due to the way this response comes back from polling vs. ws clients
  //this code is also duped as a filler for polling clients ... todo unify
  if(typeof e.data !== 'object' && e.data !== ''){

    var data = jQuery.parseJSON(e.data);
//    console.log('data.position', data.position);
    if(data !== '' && localStorage['onslyde.attendeeIP'] === data.attendeeIP){
      if(data.position === 777){
        speak.value = 'Thanks for speaking!';
      }else{
        speak.value = 'You are queued to speak';
      }
    }else{
      speak.value = 'I want to speak';
    }
  }

}, false);


window.addEventListener('roulette', function(e) {
  var rouletteDiv = document.getElementById('roulette'),
    timer1,
    timer2;
  rouletteDiv.style.display = 'block';
  if(!e.winner){
    //simple state check for multiple raffles on the same session
    if(rouletteDiv.style.backgroundColor !== 'yellow'){
      rouletteDiv.innerHTML = "<p>calculating...</p>";
      timer1 = setTimeout(function(){rouletteDiv.innerHTML = "<p>sorry! maybe next time :)</p>";},5000);
    }

  }else if(e.winner){
    setTimeout(function(){
      rouletteDiv.style.backgroundColor = 'yellow';
      rouletteDiv.innerHTML = "<p>WINNER!!...</p>";
    },5000);
  }
}, false);

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}