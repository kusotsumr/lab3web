package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * Проверяет попадание точки в область и формирует HTML-страницу с результатом.
 * Вызывается только через forward из ControllerServlet (url-mapping нет).
 */
public class AreaCheckServlet extends HttpServlet {

    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long start = System.nanoTime();
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        Double x = parse(req.getParameter("x"));
        Double y = parse(req.getParameter("y"));
        Double r = parse(req.getParameter("r"));

        String error = null;
        if (x == null || y == null || r == null) {
            error = "Параметры x, y, r должны быть заданы числами.";
        } else if (x < -3 || x > 3) {
            error = "X должен быть в диапазоне от -3 до 3.";
        } else if (y < -5 || y > 3) {
            error = "Y должен быть в диапазоне от -5 до 3.";
        } else if (r < 1 || r > 4) {
            error = "R должен быть в диапазоне от 1 до 4.";
        }

        Result result = null;
        if (error != null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            boolean hit = isHit(x, y, r);
            long micros = (System.nanoTime() - start) / 1000;
            result = new Result(x, y, r, hit, LocalDateTime.now().format(FMT), micros);
            addToSession(req.getSession(), result);
        }

        render(req, resp, result, error);
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, Result res, String error)
            throws IOException {
        String ctx = req.getContextPath();
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("<link rel=\"stylesheet\" href=\"" + ctx + "/static/style.css\">");
            out.println("<link rel=\"icon\" href=\"" + ctx + "/static/saya.jpg\">");
            out.println("<title>Результат</title></head><body>");
            out.println("<header><p>Результат проверки</p></header>");
            out.println("<section class=\"result-page\"><div class=\"results\">");

            if (error != null) {
                out.println("<p class=\"error-msg\">Ошибка: " + error + "</p>");
            } else {
                out.println("<table id=\"resultsTable\"><thead><tr>"
                        + "<th>X</th><th>Y</th><th>R</th><th>Результат</th>"
                        + "<th>Время сервера</th><th>Время работы, мкс</th></tr></thead><tbody>");
                out.println("<tr class=\"" + (res.isHit() ? "row-hit" : "row-not-hit") + "\">"
                        + "<td>" + Result.fmt(res.getX()) + "</td>"
                        + "<td>" + Result.fmt(res.getY()) + "</td>"
                        + "<td>" + Result.fmt(res.getR()) + "</td>"
                        + "<td>" + (res.isHit() ? "Входит" : "Не входит") + "</td>"
                        + "<td>" + res.getTime() + "</td>"
                        + "<td>" + res.getExecMicros() + "</td></tr>");
                out.println("</tbody></table>");
                out.println("<p class=\"verdict\">Точка " + (res.isHit() ? "попала в область" : "не попала в область") + "</p>");
            }

            out.println("<p><a class=\"back-link\" href=\"" + ctx + "/controller\">← Вернуться к форме</a></p>");
            out.println("</div></section></body></html>");
        }
    }

    @SuppressWarnings("unchecked")
    private void addToSession(HttpSession session, Result result) {
        synchronized (session) {
            List<Result> list = (List<Result>) session.getAttribute("results");
            if (list == null) {
                list = new CopyOnWriteArrayList<>();
                session.setAttribute("results", list);
            }
            list.add(result);
        }
    }

    private static Double parse(String s) {
        if (s == null) return null;
        s = s.trim().replace(',', '.');
        if (!NUMBER.matcher(s).matches()) return null;
        try {
            double d = Double.parseDouble(s);
            return Double.isFinite(d) ? d : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ---- Область (вариант 1489) ----
    // Прямоугольник: x in [-R/2, 0], y in [0, R]
    static boolean inRectangle(double x, double y, double r) {
        return x >= -r / 2 && x <= 0 && y >= 0 && y <= r;
    }

    // Четверть круга радиуса R/2 в I четверти
    static boolean inCircle(double x, double y, double r) {
        return x >= 0 && y >= 0 && x * x + y * y <= (r / 2) * (r / 2);
    }

    // Треугольник (0,0), (R,0), (0,-R/2) в IV четверти
    static boolean inTriangle(double x, double y, double r) {
        return x >= 0 && y <= 0 && y >= x / 2 - r / 2;
    }

    static boolean isHit(double x, double y, double r) {
        return inRectangle(x, y, r) || inCircle(x, y, r) || inTriangle(x, y, r);
    }
}
