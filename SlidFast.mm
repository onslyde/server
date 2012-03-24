<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1332608349036" ID="ID_1722758611" MODIFIED="1332608524952" STYLE="fork" TEXT="SlidFast">
<node CREATED="1332608509536" ID="ID_1728528902" MODIFIED="1332610686218" POSITION="left" TEXT="Business Ideas">
<node CREATED="1332608478868" ID="ID_14530766" MODIFIED="1332610596150" TEXT="Freemium Model">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Maybe gve 15 connections for free... or unlimited connections for the first 10 days, etc...
    </p>
  </body>
</html>
</richcontent>
<node CREATED="1332608490606" ID="ID_433447654" MODIFIED="1332610591463" TEXT="Based on connection limit">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      
    </p>
    <p>
      
    </p>
    <p>
      Just an idea....We could give the presenter an option of which devices they'd like to support. ws native clients would be cheapest. polling android clients would be a bit more expensive
    </p>
  </body>
</html>
</richcontent>
<node CREATED="1332610463820" ID="ID_1248489896" MODIFIED="1332610465414" TEXT="ws"/>
<node CREATED="1332610465836" ID="ID_1820461688" MODIFIED="1332610467986" TEXT="polling"/>
<node CREATED="1332610468820" ID="ID_1929883144" MODIFIED="1332610473564" TEXT="long polling"/>
<node CREATED="1332610477091" ID="ID_172863404" MODIFIED="1332610478659" TEXT="other"/>
</node>
</node>
<node CREATED="1332610687277" ID="ID_691014141" MODIFIED="1332610692300" TEXT="Conferences">
<node CREATED="1332610692797" ID="ID_1011841575" MODIFIED="1332610706490" TEXT="Data mining services for attendee data"/>
<node CREATED="1332610701637" ID="ID_885781169" MODIFIED="1332610726046" TEXT="Discount licenses for presenters"/>
<node CREATED="1332610710254" ID="ID_742898604" MODIFIED="1332610710254" TEXT=""/>
</node>
</node>
<node CREATED="1332608544067" ID="ID_885127194" MODIFIED="1332608550114" POSITION="right" TEXT="Architecture">
<node CREATED="1332608401984" ID="ID_1232294927" MODIFIED="1332608451953" TEXT="Storage">
<node CREATED="1332610305812" ID="ID_1922266961" MODIFIED="1332610322544" TEXT="Data will be stored for metrics and analytics"/>
<node CREATED="1332610323828" ID="ID_1620687795" MODIFIED="1332610328571" TEXT="what else?"/>
</node>
<node CREATED="1332608406121" ID="ID_1710423366" MODIFIED="1332608451953" TEXT="Hosting">
<node CREATED="1332610338523" ID="ID_1480268517" MODIFIED="1332610351775" TEXT="where do we host... on EC2 ourselves?"/>
</node>
<node CREATED="1332608389920" ID="ID_547245796" MODIFIED="1332609194903" TEXT="Backend">
<node CREATED="1332608394752" ID="ID_1883163034" MODIFIED="1332609565303" TEXT="Java">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Current concerns with sharing and accessing data across threads. A singleton application scoped SlidFast objectis being shared across JEtty and JBoss instances
    </p>
  </body>
</html></richcontent>
<node CREATED="1332608396889" ID="ID_1789153791" MODIFIED="1332609498992" TEXT="Jetty">
<node CREATED="1332609366002" ID="ID_1528379498" MODIFIED="1332609370265" TEXT="ws/wss provider"/>
</node>
<node CREATED="1332609325905" ID="ID_1129486044" MODIFIED="1332609330255" TEXT="JBoss AS7">
<node CREATED="1332609333698" ID="ID_1980379936" MODIFIED="1332609351198" TEXT="Using RESTEasy for polling fallback when ws is not avaiable"/>
</node>
<node CREATED="1332610604714" ID="ID_872274658" MODIFIED="1332610645507" TEXT="Whatever our new stack will be..."/>
</node>
<node CREATED="1332609374602" ID="ID_1204755911" MODIFIED="1332611139309" TEXT="JavaScript/JSON">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Controlling the events is a big concern for me. When events are sent to the browser, are they queued, or are concurrent events fired all at one - thus overwriting valid ones. i.e. 2 users vote at the same time, does only 1 vote event pass through... racey. I don't think this is happening, but I just need to be sure that the same JavaScript event can be fired concurrently and that the browser DOES queue them.
    </p>
    <p>
      
    </p>
    <p>
      Also, what is the performance of firing hundreds of events in the presenter's (slidfast slide deck) browser at any given time. All calculations are done through javascript in the slidfast slide deck. The websocket server is dumb and just giving us a way to relay the data out to connected clients.
    </p>
  </body>
</html>
</richcontent>
<node CREATED="1332609384035" ID="ID_1371378315" MODIFIED="1332609406489" TEXT="Strings are built on server then executed as Javascript events in browser"/>
</node>
</node>
<node CREATED="1332608631425" ID="ID_139519445" MODIFIED="1332608633761" TEXT="UI">
<node CREATED="1332608361898" ID="ID_870011965" MODIFIED="1332609260246" TEXT="Public">
<node CREATED="1332608595401" ID="ID_282949560" MODIFIED="1332611082261" TEXT="SlideDeck">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The slide deck Javascript is fairly solid. It needs to be refactored for reuse as there is a lot of duplicated code which was just copied and pasted
    </p>
  </body>
</html></richcontent>
<node CREATED="1332608663631" ID="ID_719494398" MODIFIED="1332608674213" TEXT="Works but history is buggy">
<node CREATED="1332608851983" ID="ID_737113910" MODIFIED="1332608870483" TEXT="Needs to implement window.location">
<node CREATED="1332608871151" ID="ID_918143875" MODIFIED="1332608878694" TEXT="will give bookmarkable URL&apos;s to slides"/>
</node>
</node>
<node CREATED="1332608999263" ID="ID_492793796" MODIFIED="1332609021069" TEXT="Slide groups should not be required to define on every data-option&quot;&quot; attribute">
<node CREATED="1332609022176" ID="ID_1488708665" MODIFIED="1332609035221" TEXT="there should be an abstract definition of group with slides under that"/>
</node>
</node>
<node CREATED="1332608602488" ID="ID_1833535819" MODIFIED="1332609923725" TEXT="Client Remote">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Client remote is simple and would only need a news skin and minor touch ups
    </p>
  </body>
</html></richcontent>
<node CREATED="1332608891787" ID="ID_1502607834" MODIFIED="1332608916712" TEXT="Needs some small fixes on informational panel transitions"/>
</node>
<node CREATED="1332608607897" ID="ID_1220509845" MODIFIED="1332609928771" TEXT="Admin Remote">
<node CREATED="1332608823600" ID="ID_701788927" MODIFIED="1332608832945" TEXT="Needs a better override capability"/>
<node CREATED="1332608939253" ID="ID_310376527" MODIFIED="1332608944100" TEXT="more responsive UI"/>
</node>
</node>
<node CREATED="1332608658766" ID="ID_1398615300" MODIFIED="1332609270438" TEXT="Admin">
<node CREATED="1332608957302" ID="ID_1871198483" MODIFIED="1332609975922" TEXT="Slide Builder">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      I think we should start with manual building of slides by user. admin panels are expensive and time consuming.
    </p>
  </body>
</html></richcontent>
<node CREATED="1332608966645" ID="ID_489729511" MODIFIED="1332608978042" TEXT="Should users build slides through a web interface?"/>
<node CREATED="1332608980022" ID="ID_1015912649" MODIFIED="1332608986020" TEXT="build them manually with HTML"/>
</node>
<node CREATED="1332608961886" ID="ID_175465177" MODIFIED="1332609052171" TEXT="Metrics and Analytics">
<node CREATED="1332609052524" ID="ID_1897103316" MODIFIED="1332609056195" TEXT="Who voted on what"/>
<node CREATED="1332609056643" ID="ID_1878864807" MODIFIED="1332609064977" TEXT="Which slides were most popular"/>
<node CREATED="1332609065565" ID="ID_1637945950" MODIFIED="1332609082964" TEXT="Attendee statistics"/>
<node CREATED="1332609084468" ID="ID_61069890" MODIFIED="1332609090204" TEXT="Tweets, etc..."/>
</node>
</node>
</node>
<node CREATED="1332609985582" ID="ID_1746838989" MODIFIED="1332610293786" TEXT="Security">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      We need to tie security into the ws events which are sent to and from the server. Maybe just prefix the JS events with a uuid based on session?
    </p>
    <p>
      
    </p>
    <p>
      Currently every event is sent to every conected client. When a vote is sent for a slidegroup, then all remote controls also receive the event (even though it is not executed on their device)...We need a way to route events to only the slidfast slide deck and only to attendee remote controls. In the end, it would be cool for the presenter to have the ability to send out specific data to clients who voted on a certain topic. e.g. If the majority of audience votes on &quot;topicA&quot;, then presenter can send links or other info to user remotes who voted on &quot;topicB&quot;..
    </p>
  </body>
</html></richcontent>
</node>
</node>
<node CREATED="1332609284620" ID="ID_1905552120" MODIFIED="1332609288403" POSITION="right" TEXT="Work Flows">
<node CREATED="1332609289339" ID="ID_559378692" MODIFIED="1332609293128" TEXT="User Sign Up">
<node CREATED="1332609293129" ID="ID_688458657" MODIFIED="1332609306337" TEXT="email + password on home page required">
<node CREATED="1332609306971" ID="ID_427295289" MODIFIED="1332610406666" TEXT="user gets instructions for building slides and slidegroups, etc..."/>
</node>
</node>
<node CREATED="1332610410611" ID="ID_1271709859" MODIFIED="1332610426276" TEXT="Presentation Start">
<node CREATED="1332610426277" ID="ID_312368744" MODIFIED="1332610444400" TEXT="User gives audience a unique URL for participation "/>
</node>
</node>
</node>
</map>
