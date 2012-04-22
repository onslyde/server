

window.addEventListener('clearRoute', function(e) {
   slidfast.slides.clearRoute();
}, false);

window.addEventListener('wtf', function(e) {
   var wtf = document.querySelector("#wtf");
   if(wtf){
      wtf.className = "show-wtf transition";
      setTimeout(function(){wtf.className = "hide-wtf transition"},800)
   }
}, false);

window.addEventListener('slideEvent', function(e) {
    if(e.action === 'next'){
        slidfast.slides.nextSlide();
    }else if (e.action === 'previous'){
        slidfast.slides.prevSlide();
    }
}, false);