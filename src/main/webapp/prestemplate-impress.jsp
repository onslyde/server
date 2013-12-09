<!doctype html>

<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=1024" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <title>onslyde - impress example presentation</title>

    <meta name="description" content="impress.js is a presentation tool based on the power of CSS3 transforms and transitions in modern browsers and inspired by the idea behind prezi.com." />
    <meta name="author" content="Bartek Szopka" />

    <link href="http://fonts.googleapis.com/css?family=Open+Sans:regular,semibold,italic,italicsemibold|PT+Sans:400,700,400italic,700italic|PT+Serif:400,700,400italic,700italic" rel="stylesheet" />


    <link href="//www.onslyde.com/js/libs/impress/impress-demo.css" rel="stylesheet" />

    <link href="https://fonts.googleapis.com/css?family=Lato:300,400,700,900" rel="stylesheet" type="text/css">
    <link href="//www.onslyde.com/css/deck.css" rel="stylesheet" />

    <link rel="apple-touch-icon" href="apple-touch-icon.png" />
</head>


<body class="impress-not-supported">
<script>

    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-31854873-2']);
    _gaq.push(['_trackPageview']);

    (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();

</script>
<div id="disagree" class="hide-disagree">
    Disagree
</div>
<div id="agree" class="hide-agree">
    Agree
</div>
<div id="stats">
    <span style="color:#fff">0<span id="wscount"></span> Connected Attendees </span>
</div>

<div class="address">
    <h4 style="text-transform: lowercase;">connect now <span style="color:#13daec;">onslyde.com/go/<span id="sessionID"></span></span></h4>
</div>
<!--
    For example this fallback message is only visible when there is `impress-not-supported` class on body.
-->
<div class="fallback-message">
    <p>Your browser <b>doesn't support the features required</b> by impress.js, so you are presented with a simplified version of this presentation.</p>
    <p>For the best experience please use the latest <b>Chrome</b>, <b>Safari</b> or <b>Firefox</b> browser.</p>
</div>


<div id="impress">

    <section class="slide-group">


        <section id="bored" class="step slide" data-x="-1000" data-y="-1500">
            <h2 class="send"><%= request.getAttribute("presName") %></h2>
            <h3><a href="http://twitter.com/<%= request.getAttribute("twitter") %>">@<%= request.getAttribute("twitter") %></a></h3>
        </section>
    </section>

    <section class="slide-group">
        <section class="step slide" data-x="0" data-y="-1500">
            <p class="send">onslyde makes you a better presenter.</p>
        </section>
    </section>

    <section class="slide-group">

        <section id="title" class="slide step" data-x="1200" data-y="600" data-rotate="180" data-option="master">
            <h3 style="color:orange" class="send"><%= request.getAttribute("poll1") %></h3>
        </section>

        <section class="slide step" data-option="<%= request.getAttribute("option1") %>"  data-x="1100" data-y="800" data-rotate="140" data-scale="5">
            <div class="send">
                This would be the <%= request.getAttribute("option1") %> track
            </div>
        </section>

        <section class="slide step" data-option="<%= request.getAttribute("option1") %>" data-x="2100" data-y="-800" data-scale="0.5">
            <div class="send">
                more <%= request.getAttribute("option1") %> track...
            </div>
        </section>

        <section class="slide step" data-option="<%= request.getAttribute("option2") %>"  data-x="3100" data-y="-1100" data-rotate="20" data-scale="5">
            <div class="send">
                This would be the <%= request.getAttribute("option2") %> track
            </div>
        </section>

        <section class="slide step" data-option="<%= request.getAttribute("option2") %>"  data-x="4100" data-y="-800" data-rotate="130" data-scale="2">
            <div class="send">
                This would be the <%= request.getAttribute("option2") %> track
            </div>
        </section>
    </section>

    <section class="slide-group">

        <section class="slide step" data-x="5500" data-y="-300" data-rotate="50" data-scale="3">
            <p><a target="_blank" href="//www.onslyde.com/#!/analytics">You can view the analytics for this session</a></p>
            <p>You can use other features (like Roulette) for random giveaways or to choose an audience member</p>
            <a href="javascript:onslyde.slides.roulette();void(0)">Pick a winner</a>
        </section>

    </section>

    <section class="slide-group" >

        <section class="slide step" data-x="6500" data-y="500" data-rotate="80" data-scale="3">
            <h3>Add more content to this presentation...</h3>
            <p>Save this page to your computer.</p>
            <p>Click File->Save Page As (make sure you select the Webpage, HTML only menu option)</p>
        </section>

    </section>


    <div id="overview" class="step" data-x="3000" data-y="1500" data-scale="10">
    </div>

</div>


<div class="hint">
    <p>Use a spacebar or arrow keys to navigate</p>
</div>
<script>
    if ("ontouchstart" in document.documentElement) {
        document.querySelector(".hint").innerHTML = "<p>Tap on the left or right to navigate</p>";
    }
</script>


<script src="//www.onslyde.com/js/libs/impress/impress.js"></script>
<script>impress().init();</script>

<script src="https://code.jquery.com/jquery-1.8.3.min.js"></script>
<script src="//www.onslyde.com/deck/js/deck/dist/onslyde-deck-1.0.0.min.js"></script>
<script>

    onslyde({
        deck: {sessionID: <%= request.getAttribute("eventid") %>, mode: 'bespoke'}
    });
</script>

<script src="//www.onslyde.com/js/libs/jquery.flot.js"></script>

</body>
</html>
