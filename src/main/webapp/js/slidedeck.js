
window.addEventListener('slideEvent', function(e) {
    if(e.action === 'next'){
        nextSlide();
    }else if (e.action === 'previous'){
        prevSlide();
    }
}, false);

