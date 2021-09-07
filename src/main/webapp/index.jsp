<%@ page contentType="text/html; charset=UTF-8" %>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We" crossorigin="anonymous">
    <title>Cinema | Hall</title>
</head>

<body>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-3.4.1.min.js" ></script>

<script>

    $(document).ready(function () {
        checkPlaces()
        setInterval(checkPlaces, 5000)
    })

    function checkPlaces() {
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8080/cinema/index.do',
            dataType: 'json'
        }).done(function (data) {
            for (var ticket of data) {
                $('#table').find('td').each(function (i, td) {
                    if ($(td).value() === (ticket.row + ticket.cell)) {
                        $(td).attr('disabled', true);
                    }
                })
            }
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

</script>

<div class="container">
    <div class="row pt-3">
        <h4>
            Бронирование мест на сеанс
        </h4>
        <select id="sessionId">
            <option label="Сеанс1" value="1" selected></option>
            <option label="Сеанс2" value="2"></option>
            <option label="Сеанс3" value="3"></option>
        </select>
        <table class="table table-bordered" id="table">
            <thead>
            <tr>
                <th style="width: 120px;">Ряд / Место</th>
                <th>1</th>
                <th>2</th>
                <th>3</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <th>1</th>
                <td><label><input type="radio" name="place" row="1" cell="1"> Ряд 1, Место 1</label></td>
                <td><label><input type="radio" name="place" row="1" cell="2"> Ряд 1, Место 2</label></td>
                <td><label><input type="radio" name="place" row="1" cell="3"> Ряд 1, Место 3</label></td>
            </tr>
            <tr>
                <th>2</th>
                <td><label><input type="radio" name="place" row="2" cell="1"> Ряд 2, Место 1</label></td>
                <td><label><input type="radio" name="place" row="2" cell="2"> Ряд 2, Место 2</label></td>
                <td><label><input type="radio" name="place" row="2" cell="3"> Ряд 2, Место 3</label></td>
            </tr>
            <tr>
                <th>3</th>
                <td><label><input type="radio" name="place" row="3" cell="1"> Ряд 3, Место 1</label></td>
                <td><label><input type="radio" name="place" row="3" cell="2"> Ряд 3, Место 2</label></td>
                <td><label><input type="radio" name="place" row="3" cell="3"> Ряд 3, Место 3</label></td>
            </tr>
            </tbody>
        </table>
    </div>
    <div class="row float-right">
        <button id="btn" type="button" class="btn btn-success" onclick="choosePlace()">Оплатить</button>
    </div>

    <div class="row float-left" id="message-block">
        <div></div>
    </div>

</div>
</body>
</html>