
window.addEventListener('slideEvent', function(e) {
    if(e.action === 'next'){
        slidfast.slides.nextSlide();
    }else if (e.action === 'previous'){
        slidfast.slides.prevSlide();
    }
}, false);

