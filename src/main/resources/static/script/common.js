$(document).ready(function () {
    let errorMsg = $.cookie('errorMsg');
    if (errorMsg != null) {
        $('.alert').html(errorMsg).addClass('alert-info').show().delay(1500).fadeOut();
        $.cookie('errorMsg', '', {path: '/', expires: -1});
        $.cookie('errorMsg', '', {path: '/success', expires: -1});
        // 访问/success/modify并设置cookie，又重定向到/success，cookie路径为'/success'
        // 访问/success并设置cookie，cookie路径为'/'
    }
})