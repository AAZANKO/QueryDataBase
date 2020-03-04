function checkParams() {
    var url = $('#url').val();
    var username = $('#username').val();
    var password = $('#password').val();

    if(url.length != 0 && username.length != 0 && password.length != 0) {
        $('#submit').removeAttr('disabled');
    } else {
        $('#submit').attr('disabled', 'disabled');
    }
}