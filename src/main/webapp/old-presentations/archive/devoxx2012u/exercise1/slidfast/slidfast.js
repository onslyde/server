/*!
 * Slidfast v0.0.1
 * www.slidfast.com
 *
 * Copyright (c) Wesley Hales
 * Available under the ASL v2.0 license (see LICENSE)
 */

//Known issues:
//1. When page "flip" is activated after accelerating a touch event,
// a double acceleration glitch occurs when flipping to the back page

// 2. Since page flip does not work on Android 2.2 - 4.0, the "front"
// and "back" concept should not be used.

//optimize for minification and performance
(function (window, document, undefined) {
  "use strict";
  window.slidfast = (function () {

    var options,

      slidfast = function (startupOptions) {
        options = startupOptions;
        return new slidfast.core.init();
      },

      defaultPageID = null,

      focusPage = null,

      flipped = false;

    slidfast.core = slidfast.prototype = {
      constructor: slidfast,

      start: function () {

        try {
          if (options) {
            //setup all the options being passed in in the init
            defaultPageID = options.defaultPageID;

          }
        } catch (e) {
          //alert('Problem with init. Check your options: ' + e);
        }

        slidfast.core.hideURLBar();
      },

      hideURLBar:function () {
        //hide the url bar on mobile devices
        setTimeout(scrollTo, 0, 0, 1);
      },

      init:function () {

        window.addEventListener('load', function (e) {
          slidfast.core.start();
        }, false);

        return slidfast.core;

      }

    };

    slidfast.core.init.prototype = slidfast.core;

    slidfast.ui = slidfast.prototype = {

      slideTo:function (id, callback) {
        if (!focusPage) {
          focusPage = getElement(defaultPageID);
        }

        //1.)the page we are bringing into focus dictates how
        // the current page will exit. So let's see what classes
        // our incoming page is using. We know it will have stage[right|left|etc...]
        if (typeof id === 'string') {
          try {
            id = getElement(id);
          } catch (e) {
            //console.log('You can\'t slideTo that element, because it doesn\'t exist');
          }
        }

        var classes;
        //todo use classList here
        //this causes error with no classname--> console.log(id.className.indexOf(' '));
        try {
          classes = id.className.split(' ');
        } catch (e) {
          //console.log('problem with classname on .page: ' + id.id);
        }

        //2.)decide if the incoming page is assigned to right or left
        // (-1 if no match)
        var stageType = classes.indexOf('stage-left');

        //3a.)Flip if needed
        var front = getElement('front');
        if (front) {
          var frontNodes = front.getElementsByTagName('*');
          for (var i = 0; i < frontNodes.length; i += 1) {
            if (id.id === frontNodes[i].id && flipped) {
              slidfast.ui.flip();
            }
          }
        }

        //3b.) decide how this focused page should exit.
        if (stageType > 0) {
          focusPage.className = 'page transition stage-right';
        } else {
          focusPage.className = 'page transition stage-left';
        }

        //4. refresh/set the variable
        focusPage = id;

        //5. Bring in the new page.
        focusPage.className = 'page transition stage-center';

        //6. make this transition bookmarkable
        slidfast.core.locationChange(focusPage.id);

        if (callback) {
          //time of transition - todo convert css to javascript here
          //we're creating a way to have a callback at the end of the transition/page slide
          setTimeout(callback, 500);
        }


      },


      flip:function () {
        //get a handle on the flippable region
        var front = document.getElementById('front');
        var back = document.getElementById('back');

        //just a simple way to see what the state is
        var classes = front.className.split(' ');
        var flippedClass = classes.indexOf('flipped');

        if (flippedClass >= 0) {
          //already flipped, so return to original
          front.className = 'normal';
          back.className = 'flipped';
          flipped = false;
        } else {
          //do the flip
          front.className = 'flipped';
          back.className = 'normal';
          flipped = true;
        }
      }

    };

    var getElement = function (id) {
      if (document.querySelector) {
        return document.querySelector('#' + id);
      } else {
        return document.getElementById(id);
      }
    };

    return slidfast;

  }());
}(window, document));


