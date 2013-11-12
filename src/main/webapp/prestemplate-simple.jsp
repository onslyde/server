<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">

    <title><%= request.getAttribute("presName") %></title>

    <meta name="description" content="onslyde example">
    <meta name="author" content="">

    <meta name="apple-mobile-web-app-capable" content="yes" />
    <!-- controls the appearance of the status bar in full-screen mode -->
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />

    <link rel="stylesheet" media="all" href="http://onslyde.com/css/slidfast.css" />
    <link href="http://onslyde.com/css/deck.css" rel="stylesheet" />


    <style>
        .placeholder {
            height: 125px;
            width: 85%;
        }
    </style>
</head>
<body>
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
    <span style="color:#fff"><span id="wscount"></span> Connected Attendees</span>
</div>

<div class="address">
    <h4 style="text-transform: lowercase;">connect now <span style="color:#13daec;">http://onslyde.com/go/<span id="sessionID"></span></span></h4>
</div>
<!-- Any section element inside of this container is displayed as a slide -->
<div class="slides" id="slidfast">

    <section class="slide-group">

        <section class="slide">
            <h2 class="send"><%= request.getAttribute("presName") %></h2>
            <h3><a href="http://twitter.com/<%= request.getAttribute("twitter") %>">@<%= request.getAttribute("twitter") %></a></h3>
        </section>

    </section>

    <section class="slide-group">

        <section class="slide">
            <p class="send">onslyde makes you a better presenter.</p>
        </section>

    </section>

    <section class="slide-group">

        <section class="slide" data-option="master">
            <h3 style="color:orange" class="send"><%= request.getAttribute("poll1") %></h3>
        </section>

        <section class="slide" data-option="<%= request.getAttribute("option1") %>">
            <div class="send">
                This would be the <%= request.getAttribute("option1") %> track
            </div>
        </section>

        <section class="slide" data-option="<%= request.getAttribute("option1") %>">
            <div class="send">
                more <%= request.getAttribute("option1") %> track...
            </div>
        </section>

        <section class="slide" data-option="<%= request.getAttribute("option2") %>">
            <div class="send">
                This would be the <%= request.getAttribute("option2") %> track
            </div>
        </section>

        <section class="slide" data-option="<%= request.getAttribute("option2") %>">
            <div class="send">
                more <%= request.getAttribute("option2") %> track...
            </div>
        </section>

    </section>

    <section class="slide-group">

        <section>
            <p><a target="_blank" href="http://onslyde.com/#!/analytics">You can view the analytics for this session</a></p>
            <p>You can use other features (like Roulette) for random giveaways or to choose an audience member</p>
            <a href="javascript:onslyde.slides.roulette();void(0)">Pick a winner</a>
        </section>

    </section>

    <section class="slide-group">

        <section>
            <h3>Add more content to this presentation...</h3>
            <p>Save this page to your computer.</p>
            <p>Click File->Save Page As (make sure you select the Webpage, HTML only menu option)</p>
        </section>

    </section>


</div>

<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
<script src="http://onslyde.com/deck/js/deck/dist/onslyde-deck-1.0.0.min.js"></script>
<script>

    onslyde({
        deck: {sessionID: <%= request.getAttribute("eventid") %>, mode: 'default'}
    });
</script>

<script src="http://onslyde.com/js/libs/jquery.flot.js"></script>
</body>
</html>