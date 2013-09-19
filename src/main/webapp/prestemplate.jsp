<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8">

    <title><%= request.getAttribute("presName") %></title>

    <meta name="description" content="onslyde example">

    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />

    <link rel="stylesheet" href="http://onslyde.com/css/reveal/reveal.css">
    <link rel="stylesheet" href="http://onslyde.com/css/reveal/theme/default.css" id="theme">

    <!-- For syntax highlighting -->
    <link rel="stylesheet" href="http://onslyde.com/js/reveal/lib/css/zenburn.css">

    <!-- If the query includes 'print-pdf', use the PDF print sheet -->
    <script>
        document.write( '<link rel="stylesheet" href="http://onslyde.com/css/reveal/print/' + ( window.location.search.match( /print-pdf/gi ) ? 'pdf' : 'paper' ) + '.css" type="text/css" media="print">' );
    </script>

    <!--[if lt IE 9]>
    <script src="http://onslyde.com/js/reveal/lib/js/html5shiv.js"></script>
    <![endif]-->
    <link rel="stylesheet" href="http://onslyde.com/css/deck.css">
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
    <span><span id="wscount"></span> WebSocket </span><span><span id="pollcount"></span> Polling</span>
</div>
<div class="reveal">
    <div class="address">
        <h4 style="text-transform: lowercase;">connect now <span style="color:#13daec;">http://onslyde.com/go/<span id="sessionID"></span></span></h4>
    </div>
    <!-- Any section element inside of this container is displayed as a slide -->
    <div class="slides">

        <section class="slide-group">

            <section>
                <h2 class="send"><%= request.getAttribute("presName") %></h2>
                <h3><a href="http://twitter.com/<%= request.getAttribute("twitter") %>">@<%= request.getAttribute("twitter") %></a></h3>
            </section>

        </section>

        <section class="slide-group">

            <section>
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
                <h3>Add more content to this presentation...</h3>
                <p>Save this page to your computer.</p>
                <p>Click File->Save Page As (make sure you select the Webpage, HTML only menu option)</p>
            </section>

        </section>


    </div>


</div>

<script src="http://onslyde.com/js/reveal/lib/js/head.min.js"></script>
<script src="http://onslyde.com/js/reveal/reveal.min.js"></script>

<script>

    // Full list of configuration options available here:
    // https://github.com/hakimel/reveal.js#configuration
    Reveal.initialize({
        controls: true,
        progress: true,
        history: true,

        theme: Reveal.getQueryHash().theme, // available themes are in /css/theme
        transition: Reveal.getQueryHash().transition || 'default', // default/cube/page/concave/zoom/linear/none

        // Optional libraries used to extend on reveal.js
        dependencies: [
            { src: 'http://onslyde.com/js/reveal/lib/js/classList.js', condition: function() { return !document.body.classList; } },
            { src: 'http://onslyde.com/js/reveal/plugin/markdown/showdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
            { src: 'http://onslyde.com/js/reveal/plugin/markdown/markdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
            { src: 'http://onslyde.com/js/reveal/plugin/highlight/highlight.js', async: true, callback: function() { hljs.initHighlightingOnLoad(); } },
            { src: 'http://onslyde.com/js/reveal/plugin/zoom-js/zoom.js', async: true, condition: function() { return !!document.body.classList; } },
            { src: 'http://onslyde.com/js/reveal/plugin/notes/notes.js', async: true, condition: function() { return !!document.body.classList; } }
        ]
    });

</script>

<script src="http://onslyde.com/js/onslyde-1.0.0.deck.js?v=1"></script>
<script>

    slidfast({
        onslyde: {deck:true,sessionID: <%= request.getAttribute("eventid") %>, mode:'reveal'}
    });
</script>
<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
<script src="http://onslyde.com/js/libs/jquery.flot.js"></script>
<script src="http://onslyde.com/js/deck.js?v=1"></script>

</body>
</html>
