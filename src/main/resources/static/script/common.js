$(document).ready(function () {
    $('#deleteDiary').click(function () {
        $('.deleteDiv').css('display', 'block');
    })
    $('#message-submit').click(function () {
        let content = $('#message-content').val();
        let videoId = $('#videoId').val();
        $.ajax({
            url: "/theater/watch/comment",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                content: content,
                videoId: videoId
            }),
            dataType: 'json',
            success:function (res) {
                if (res['success'] === "0") $('.alert').html('fail').addClass('alert-info').show().delay(1500).fadeOut();
                else {
                    $("<li class=\"list-group-item\" style=\"background: black; color: white;\">"
                        + "#" + res['time'] + "#&nbsp[" + res['name'] + "]:&nbsp&nbsp" +  content  + "</li>").insertAfter('#newComment');
                    $('.alert').html('success').addClass('alert-info').show().delay(1000).fadeOut();
                }
            }
        })
    })
    $('#message-load').click(function () {
        $('#more-messageList').css("display", "block");
    })
})