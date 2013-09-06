var speak = document.querySelector('#speak'),
  disagree = document.querySelector('#disagree'),
  agree = document.querySelector('#agree'),
  voteLabel = document.querySelector('#vote-label'),
  voted,
  isSpeaking = false;

disablePoll();

speak.onclick = function (event) {
  _gaq.push(['_trackEvent', 'onslyde-speak', 'vote']);
  if (userObject.name === '') {
    speak.onclick = handleAuthClick;
  } else {
    ws.send('speak:' + JSON.stringify(userObject));
    speak.value = 'Remove yourself from queue';
  }


};

var agreeTimeout,
  disagreeTimeout,
  disagreeInterval,
  agreeInterval;

disagree.onclick = function (event) {
  _gaq.push(['_trackEvent', 'onslyde-disagree', 'vote']);
  ws.send('props:disagree,' + userObject.name + "," + userObject.email);
  disagree.disabled = true;
  disagree.style.opacity = .4;

  clearTimeout(disagreeTimeout);
  disagreeTimeout = setTimeout(function () {
    disagree.disabled = false;
    disagree.style.opacity = 1;
    disagree.value = 'Disagree';
    clearInterval(disagreeInterval);
  }, 30000);
  var counter = 30;
  disagreeInterval = setInterval(function(){
    disagree.value = 'vote again in ' + counter + ' seconds';
    counter--;
  },1000);
  return false;
};

agree.onclick = function (event) {
  _gaq.push(['_trackEvent', 'onslyde-agree', 'vote']);
  ws.send('props:agree,' + userObject.name + "," + userObject.email);
  agree.disabled = true;
  agree.style.opacity = .4;
  agree.value = "vote again in 30 seconds";
  clearTimeout(agreeTimeout);
  agreeTimeout = setTimeout(function () {
    agree.disabled = false;
    agree.style.opacity = 1;
    agree.value = 'Agree';
    clearInterval(agreeInterval);
  }, 30000);
  var counter = 30;
  agreeInterval = setInterval(function(){
    agree.value = 'vote again in ' + counter + ' seconds';
    counter--;
  },1000);
  return false;
};

function disablePoll() {
  disagree.disabled = true;
  disagree.disabled = true;
  agree.style.opacity = .4;
  agree.style.opacity = .4;

  speak.disabled = true;
  speak.style.opacity = .4;
  voteLabel.innerHTML = 'Waiting...';
}

function enablePoll() {

  if(!isSpeaking){
    speak.disabled = false;
    speak.value = 'I want to speak';
    speak.style.opacity = 1;
  }
  clearTimeout(agreeTimeout);
  clearInterval(agreeInterval);
    agree.disabled = false;
    agree.style.opacity = 1;
    agree.value = 'Agree';
//  }

  clearTimeout(disagreeTimeout);
  clearInterval(disagreeInterval);
    disagree.disabled = false;
    disagree.value = 'Disagree';
    disagree.style.opacity = 1;
//  }




  voteLabel.innerHTML = 'Vote!';
  voted = false;
}

window.addEventListener('updateOptions', function (e) {
  enablePoll();
}, false);

//callback for pressing the speak button (managed server side)
window.addEventListener('speak', function (e) {
  handleSpeakEvent(e);
}, false);

var resetTimeout;

function handleSpeakEvent(e) {
  if (e.position === '777') {
    isSpeaking = true;
    speak.value = 'You\'re speaking!';
    speak.disabled = true;
    speak.style.opacity = .4;
    resetTimeout = setTimeout(function () {
      isSpeaking = false;
      speak.value = 'I want to speak';
      speak.disabled = false;
      clearTimeout(resetTimeout);
    }, 20000);
  } else {

  }
}

window.addEventListener('remoteMarkup', function (e) {
//  console.log('e', typeof e.markup);

  if (e.markup !== '') {
    var markup = JSON.parse(e.markup);
    try {
      document.getElementById('from-slide').innerHTML = decodeURIComponent(markup.remoteMarkup);
    } catch (e) {
    }
  }

  //checking for type of object due to the way this response comes back from polling vs. ws clients
  //this code is also duped as a filler for polling clients ... todo unify
  if (typeof e.data !== 'object' && e.data !== '') {

    var data = JSON.parse(e.data);
    console.log('data.position', data.position, localStorage['onslyde.attendeeIP'], localStorage['onslyde.attendeeIP'] === data.attendeeIP);
    if (data !== '' && localStorage['onslyde.attendeeIP'] === data.attendeeIP) {
      handleSpeakEvent(data);
    } else {
      speak.value = 'I want to speak';
    }
  }

}, false);


window.addEventListener('roulette', function (e) {
  var rouletteDiv = document.getElementById('roulette'),
    timer1,
    timer2;
  rouletteDiv.style.display = 'block';
  if (!e.winner) {
    //simple state check for multiple raffles on the same session
    if (rouletteDiv.style.backgroundColor !== 'yellow') {
      rouletteDiv.innerHTML = "<p>calculating...</p>";
      timer1 = setTimeout(function () {
        rouletteDiv.innerHTML = "<p>sorry! maybe next time :)</p>";
      }, 5000);
    }

  } else if (e.winner) {
    setTimeout(function () {
      rouletteDiv.style.backgroundColor = 'yellow';
      rouletteDiv.innerHTML = "<p>WINNER!!...</p>";
    }, 5000);
  }
}, false);

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}