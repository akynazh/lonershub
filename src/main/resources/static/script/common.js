$(document).ready(function () {
    let errorMsg = $.cookie('errorMsg');
    if (errorMsg != null) {
        let myPath = window.location.host + '/';
        // window.location.host:
        // 'https://akynazh.site/p/linux%E5%AE%9E%E7%94%A8%E6%93%8D%E4%BD%9C%E5%9B%9B/' -> 'akynazh.site'
        // 'http://localhost:8080/index' -> 'localhost:8080'
        $('.alert').html(errorMsg).addClass('alert-info').show().delay(1500).fadeOut();
        $.cookie('errorMsg', '', {path: myPath, expires: -1});
        // '/'似乎不能指定为所有路径, 而使用完整路径则可以
    }
    $('#deleteDiary').click(function () {
        $('.deleteDiv').css('display', 'block');
    })
})