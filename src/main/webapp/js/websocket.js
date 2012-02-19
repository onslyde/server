function getParameterByName(name) {

var match = RegExp('[?&]' + name + '=([^&]*)')
                .exec(window.location.search);

return match && decodeURIComponent(match[1].replace(/\+/g, ' '));

}

if (!window.WebSocket)
    alert("WebSocket not supported by this browser");

function $() {
    return document.getElementById(arguments[0]);
}
function $F() {
    return document.getElementById(arguments[0]).value;
}

function getKeyCode(ev) {
    if (window.event)
        return window.event.keyCode;
    return ev.keyCode;
}

var eventObj = document.createEvent('Event');

var room = {
    join : function(name) {
        this._username = name;
        //var location = document.location.toString().replace('http://',
        //		'ws://').replace('https://', 'wss://');
        var location = "ws://192.168.1.101:8081/"
        this._ws = new WebSocket(location);
        this._ws.onopen = this._onopen;
        this._ws.onmessage = this._onmessage;
        this._ws.onclose = this._onclose;
        this._ws.onerror = this._onerror;
    },

    chat : function(text) {
        if (text != null && text.length > 0)
            room._send(room._username, text);
    },

    _onopen : function() {
        //basic auth until we get something better
        room._send('user:'+room._username);
    },

    _onmessage : function(m) {
        if (m.data) {

            //check to see if this message is a CDI event
            if(m.data.indexOf('cdievent') > 0){
                try{
                    //$('log').innerHTML = m.data;

                    //avoid use of eval...
                    var event = (m.data);
                    event = (new Function("return " + event))();
                    event.cdievent.fire();
                }catch(e){
                    alert(e);
                }
            }else{
//                var c = m.data.indexOf(':');
//                var from = m.data.substring(0, c).replace('<', '&lt;').replace(
//                        '>', '&gt;');
//                var text = m.data.substring(c + 1).replace('<', '&lt;')
//                        .replace('>', '&gt;');
//
//                var chat = $('chat');
//                var spanFrom = document.createElement('span');
//                spanFrom.className = 'from';
//                spanFrom.innerHTML = from + ':&nbsp;';
//                var spanText = document.createElement('span');
//                spanText.className = 'text';
//                spanText.innerHTML = text;
//                var lineBreak = document.createElement('br');
//                chat.appendChild(spanFrom);
//                chat.appendChild(spanText);
//                chat.appendChild(lineBreak);
//                chat.scrollTop = chat.scrollHeight - chat.clientHeight;
            }
        }
    },

    _onclose : function(m) {
        this._ws = null;
//        $('join').className = '';
//        $('joined').className = 'hidden';
//        $('username').focus();
//        $('chat').innerHTML = '';
    },

    _onerror : function(e) {
        alert(e);
    },

    _send : function(user, message) {
        user = user.replace(':', '_');
        if (this._ws)
            this._ws.send(user + ':' + message);
    }
};



window.addEventListener('memberEvent', function(e) {
    alert(e.name + ' just registered!');
}, false);

window.addEventListener('load', function(e) {
    if(getParameterByName('username')){
        room.join(getParameterByName('username'));
    }else{
        room.join('anonymous');
    }
}, false);

