// 1) When user is NOT connected, 'join' div is displayed with Username input + Join button
$('username').setAttribute('autocomplete', 'OFF');
// Enter in the username field, opens Chat WebSockets
$('username').onkeyup = function(ev) {
    var keyc = getKeyCode(ev);
    if (keyc == 13 || keyc == 10) {
        room.join($F('username'));
        return false;
    }
    return true;
};
// "Join" button click opens Chat WebSockets
$('joinB').onclick = function(event) {
    room.join($F('username'));
    return false;
};

// 2) When user is connected, 'phrase' div is displayed with Chat input + Send button
$('phrase').setAttribute('autocomplete', 'OFF');
// Enter in the 'phrase' field send the message
$('phrase').onkeyup = function(ev) {
    var keyc = getKeyCode(ev);
    if (keyc == 13 || keyc == 10) {
        room.chat($F('phrase'));
        $('phrase').value = '';
        return false;
    }
    return true;
};
// "Send" button click send the message
$('sendB').onclick = function(event) {
    room.chat($F('phrase'));
    $('phrase').value = '';
    return false;
};

