<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">

    <title>onslyde - bespoke example presentation</title>

    <meta name="description" content="onslyde example">
    <meta name="author" content="Wesley Hales">

    <meta name="apple-mobile-web-app-capable" content="yes" />
    <!-- controls the appearance of the status bar in full-screen mode -->
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />

    <link rel="stylesheet" type="text/css" href="/js/libs/bespoke/style.css" />
    <link rel="stylesheet" type="text/css" href="/js/libs/bespoke/themes.css" />

    <!--onslyde specific styles-->
    <link href="http://fonts.googleapis.com/css?family=Lato:300,400,700,900" rel="stylesheet" type="text/css">
    <link href="/css/deck.css" rel="stylesheet" />


    <style>
        .placeholder {
            height: 125px;
            width: 85%;
        }
    </style>
</head>
<body class="coverflow">
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
    <span><span id="wscount">0</span> Connected Attendees</span>
</div>

<div class="address">
    <h4 style="text-transform: lowercase;">connect now <span style="color:#13daec;">http://onslyde.com/go/<span id="sessionID"></span></span></h4>
</div>
<!-- Any section element inside of this container is displayed as a slide -->
<div id="main">
    <header>
        <p class="hidden"><span id="input-method">Press up and down keys</span> to view example themes.</p>
    </header>
    <article>

        <span class="slide-group">

            <section class="slide">
                <h2 class="send"><%= request.getAttribute("presName") %></h2>
                <h3><a href="http://twitter.com/<%= request.getAttribute("twitter") %>">@<%= request.getAttribute("twitter") %></a></h3>
            </section>

        </span>

        <span class="slide-group">

            <section class="slide">
                <p class="send">onslyde makes you a better presenter.</p>
            </section>

        </span>

        <span class="slide-group">

            <section class="slide" data-option="master">
                <h2 class="send"><%= request.getAttribute("poll1") %></h2>
            </section>

            <section class="slide" data-option="<%= request.getAttribute("option1") %>">
                <p class="send">
                    This would be the <%= request.getAttribute("option1") %> track
                </p>
            </section>

            <section class="slide" data-option="<%= request.getAttribute("option2") %>">
                <p class="send">
                    This would be the <%= request.getAttribute("option2") %> track
                </p>
            </section>

            <section class="slide" data-option="<%= request.getAttribute("option2") %>">
                <p class="send">
                    more <%= request.getAttribute("option2") %> track...
                </p>
            </section>

        </span>

        <span class="slide-group">

            <section class="slide">
                <p><a target="_blank" href="http://onslyde.com/#!/analytics">You can view the analytics for this session</a></p>
                <p>You can use other features (like Roulette) for random giveaways or to choose an audience member</p>
                <a href="javascript:onslyde.slides.roulette();void(0)">Pick a winner</a>
            </section>

        </span>

        <span class="slide-group">

            <section class="slide">
                <h3>Add more content to this presentation...</h3>
                <p>Save this page to your computer.</p>
                <p>Click File->Save Page As (make sure you select the Webpage, HTML only menu option)</p>
            </section>

        </span>


    </article>
</div>

<footer style="opacity:0">
    <div class="credits">

    </div>

    <div class="themes">
        <p>
            <strong>Example Theme<span class="colon">:</span></strong> <span id="theme">Carousel</span>
        </p>
        <div id="up-arrow" class="up arrow">^</div>
        <div id="down-arrow" class="down arrow">^</div>
    </div>

</footer>

<script src="/js/libs/bespoke/bespoke.js"></script>
<script src="/js/libs/bespoke/demo.js"></script>
<script src="/js/onslyde-1.0.0.deck.js"></script>
<script>

    onslyde({
        deck: {sessionID: <%= request.getAttribute("eventid") %>, mode: 'bespoke'}
    });
</script>
<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
<script src="/js/libs/jquery.flot.js"></script>
<script src="/js/deck.js"></script>
</body>
</html>