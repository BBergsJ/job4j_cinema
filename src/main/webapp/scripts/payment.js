$(document).ready(function () {
    let searchParams = new URLSearchParams(window.location.search)
    let sessionId = searchParams.get('sessionId')
    let row = searchParams.get('row')
    let cell = searchParams.get('cell')
    $('#row').text(`${row}`)
    $('#cell').text(`${cell}`)
});

function validate() {
    const username =$('#username')
    const email =$('#email')
    const phone =$('#phone')
    if (username.val() === '') {
        alert("Not filled: " + username.attr('id'));
        return false;
    }
    if (email.val() === '') {
        alert("Not filled: " + email.attr('id'));
        return false;
    }
    if (phone.val() === '') {
        alert("Not filled: " + phone.attr("id"));
        return false;
    }
    return true;
}

function submitForm() {
    if (validate()) {
        let searchParams = new URLSearchParams(window.location.search)
        let sessionId = searchParams.get('sessionId')
        let row = searchParams.get('row')
        let cell = searchParams.get('cell')
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/cinema/index.do',
            data: JSON.stringify({
                username: $('#username').val(),
                email: $('#email').val(),
                phone: $('#phone').val(),
                sessionId: sessionId,
                row: row,
                cell: cell
            }),
            dataType: 'json',
            success: function (data) {
                let msg = data.message
                $('#message-block').html('<div class="alert alert-success">' + msg + '</div>')
            },
            error: function (jqXHR) {
                if (jqXHR.status === 409) {
                    alert('Место занято! Выберите другое место.')
                } else {
                    alert('Непредвиденная ошибка!')
                }
            }
        });
    }
}