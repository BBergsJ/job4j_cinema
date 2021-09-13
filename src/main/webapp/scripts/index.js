$(document).ready(function () {
    checkPlaces()
    setInterval(checkPlaces, 5000)
})

function checkPlaces() {
    let sessionId = Number.parseInt($('#sessionId').val())
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/cinema/index.do',
        dataType: 'json'
    }).done(function (data) {
        $.each($('td'), (i, tdi) => {
            let input = tdi.children[0].children[0];
            let tdRow = Number.parseInt(input.getAttribute('row'))
            let tdCell = Number.parseInt(input.getAttribute('cell'))
            for (let j = 0; j < data.length; j++) {
                if ( sessionId === data[j].sessionId && tdRow === data[j].row && tdCell === data[j].cell) {
                    tdi.classList.add('table-danger')
                    tdi.classList.remove('table-success')
                    input.disabled = true
                    break
                } else {
                    tdi.classList.remove('table-danger')
                    tdi.classList.add('table-success')
                    input.disabled = false
                }
            }
        })
    }).fail(function (err) {
        console.log(err)
    });
}

function choosePlace() {
    let radioField = document.querySelector("input[name=place]:checked")
    if($(radioField).length!==0) {
        let URL = 'http://localhost:8080/cinema/payment.html'
        let sessionId = $('#sessionId').val()
        let row = radioField.getAttribute("row")
        let cell = radioField.getAttribute("cell")
        window.location.href = URL + '?sessionId=' + sessionId + '&row=' + row + '&cell=' + cell
    } else {
        $('#message-block').html(`<div class="alert alert-danger">
    <strong>Внимание!</strong> Выберите место!</div>`)
    }
}
