<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.*,org.example.Result" %>
<%
    List<Result> stored = (List<Result>) session.getAttribute("results");
    List<Result> results = stored == null ? new ArrayList<Result>() : new ArrayList<Result>(stored);
    String lastR = results.isEmpty() ? "" : Result.fmt(results.get(results.size() - 1).getR());
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<%= ctx %>/static/style.css">
    <link rel="icon" type="image/x-icon" href="<%= ctx %>/static/saya.jpg">
    <title>Cat</title>
</head>
<body>
<header>
    <p><a href="https://t.me/catmeonmeoncat" style="text-decoration: none; color: inherit; cursor: pointer;">ФИО: Семенова Елена Вячеславовна</a></p>
    <p>Группа: P3213</p>
    <p>Вариант: 1489</p>
</header>
<section>
    <div class="cat">
        <form id="pointForm" action="<%= ctx %>/controller" method="get" autocomplete="off">
            <div>
                <label for="x-input">X (от -3 до 3): </label>
                <input id="x-input" type="text" name="x">
            </div>
            <div>
                <span>Y:</span>
                <button type="button" class="y-btn" data-value="-5">-5</button>
                <button type="button" class="y-btn" data-value="-4">-4</button>
                <button type="button" class="y-btn" data-value="-3">-3</button>
                <button type="button" class="y-btn" data-value="-2">-2</button>
                <button type="button" class="y-btn" data-value="-1">-1</button>
                <button type="button" class="y-btn" data-value="0">0</button>
                <button type="button" class="y-btn" data-value="1">1</button>
                <button type="button" class="y-btn" data-value="2">2</button>
                <button type="button" class="y-btn" data-value="3">3</button>
                <input type="hidden" id="y-input" name="y">
            </div>
            <div>
                <label for="r-input">R (от 1 до 4): </label>
                <input id="r-input" type="text" name="r" value="<%= lastR %>">
            </div>
            <div id="error" class="error-msg"></div>
            <button id="Change" type="submit">Нажми</button>
        </form>

        <canvas id="myCanvas" width="400" height="400"></canvas>
    </div>
    <div class="results">
        <div class="table-wrap">
            <table id="resultsTable">
                <thead>
                <tr>
                    <th>X</th>
                    <th>Y</th>
                    <th>R</th>
                    <th>Результат</th>
                    <th>Время сервера</th>
                    <th>Время работы, мкс</th>
                </tr>
                </thead>
                <tbody>
                <% for (int i = results.size() - 1; i >= 0; i--) {
                    Result res = results.get(i); %>
                <tr class="<%= res.isHit() ? "row-hit" : "row-not-hit" %>">
                    <td><%= Result.fmt(res.getX()) %></td>
                    <td><%= Result.fmt(res.getY()) %></td>
                    <td><%= Result.fmt(res.getR()) %></td>
                    <td><%= res.isHit() ? "Входит" : "Не входит" %></td>
                    <td><%= res.getTime() %></td>
                    <td><%= res.getExecMicros() %></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</section>
<script>
    // Точки из HTTP-сессии (для отрисовки на графике)
    window.CONTROLLER_URL = "<%= ctx %>/controller";
    window.POINTS = [
        <% for (Result res : results) { %>
        {x: <%= Result.fmt(res.getX()) %>, y: <%= Result.fmt(res.getY()) %>, hit: <%= res.isHit() %>},
        <% } %>
    ];
</script>
<script src="<%= ctx %>/static/script.js"></script>
</body>
</html>
