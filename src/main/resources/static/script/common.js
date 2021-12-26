$(document).ready(function () {
    let url = window.location.pathname;
    let url1 = url.substr(1, url.length-1);
    let pos = url1.indexOf('/');
    if (pos < 0) pos = url.length;
    else pos++;0
    window.my_url = url.substring(0, pos);
})