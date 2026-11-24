const SIZE = 400;
const C = SIZE / 2;       
const UNIT = 40;           

const canvas = document.getElementById("myCanvas");
const ctx = canvas.getContext("2d");
const form = document.getElementById("pointForm");
const xInput = document.getElementById("x-input");
const yInput = document.getElementById("y-input");
const rInput = document.getElementById("r-input");
const errorBox = document.getElementById("error");
const yButtons = document.querySelectorAll(".y-btn");

const px = (x) => C + x * UNIT;
const py = (y) => C - y * UNIT;

const NUM_RE = /^-?\d+([.,]\d+)?$/;

function parseNum(text) {
    const t = text.trim();
    if (!NUM_RE.test(t)) return null;
    return Number(t.replace(",", "."));
}

function getR() {
    const r = parseNum(rInput.value);
    return r !== null && r >= 1 && r <= 4 ? r : null;
}

function showError(msg) {
    errorBox.textContent = msg;
}

function drawFigure(r) {
    ctx.fillStyle = "#d158b3";


    ctx.beginPath();
    ctx.rect(px(-r / 2), py(r), (r / 2) * UNIT, r * UNIT);
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(px(0), py(0));
    ctx.lineTo(px(0), py(r / 2));
    ctx.arc(px(0), py(0), (r / 2) * UNIT, -Math.PI / 2, 0, false);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(px(0), py(0));
    ctx.lineTo(px(r), py(0));
    ctx.lineTo(px(0), py(-r / 2));
    ctx.closePath();
    ctx.fill();
}

function drawAxes(r) {
    const end = C - 10;
    ctx.strokeStyle = "#000";
    ctx.fillStyle = "#000";
    ctx.lineWidth = 1;

    ctx.beginPath();
    ctx.moveTo(C - end, C); ctx.lineTo(C + end, C);
    ctx.moveTo(C, C - end); ctx.lineTo(C, C + end);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(C + end, C); ctx.lineTo(C + end - 8, C - 4); ctx.lineTo(C + end - 8, C + 4);
    ctx.closePath(); ctx.fill();
    ctx.beginPath();
    ctx.moveTo(C, C - end); ctx.lineTo(C - 4, C - end + 8); ctx.lineTo(C + 4, C - end + 8);
    ctx.closePath(); ctx.fill();

    ctx.font = "12px sans-serif";
    ctx.fillText("x", C + end - 10, C + 16);
    ctx.fillText("y", C + 8, C - end + 10);

    for (let i = -4; i <= 4; i++) {
        if (i === 0) continue;
        ctx.beginPath();
        ctx.moveTo(px(i), C - 3); ctx.lineTo(px(i), C + 3);
        ctx.moveTo(C - 3, py(i)); ctx.lineTo(C + 3, py(i));
        ctx.stroke();
    }

    if (r !== null) {
        const marks = [
            [r, "R"], [r / 2, "R/2"], [-r / 2, "-R/2"], [-r, "-R"]
        ];
        ctx.strokeStyle = "#000";
        for (const [v, label] of marks) {
            ctx.beginPath();
            ctx.moveTo(px(v), C - 6); ctx.lineTo(px(v), C + 6);
            ctx.moveTo(C - 6, py(v)); ctx.lineTo(C + 6, py(v));
            ctx.stroke();
            ctx.fillText(label, px(v) - 8, C + 20);
            ctx.fillText(label, C + 10, py(v) + 4);
        }
    }
}

function drawPoints() {
    for (const p of window.POINTS) {
        const x = px(p.x), y = py(p.y);
        if (x < 0 || x > SIZE || y < 0 || y > SIZE) continue;
        ctx.beginPath();
        ctx.arc(x, y, 4, 0, 2 * Math.PI);
        ctx.fillStyle = p.hit ? "green" : "red";
        ctx.fill();
    }
}

function redraw() {
    const r = getR();
    ctx.clearRect(0, 0, SIZE, SIZE);
    if (r !== null) drawFigure(r);
    drawAxes(r);
    drawPoints();
}

rInput.addEventListener("input", () => {
    showError("");
    redraw();
});

yButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
        yButtons.forEach((b) => b.classList.remove("selected"));
        btn.classList.add("selected");
        yInput.value = btn.dataset.value;
        showError("");
    });
});

canvas.addEventListener("click", (e) => {
    const r = getR();
    if (r === null) {
        showError("Невозможно определить координаты точки: сначала задайте радиус R (от 1 до 4).");
        return;
    }
    const rect = canvas.getBoundingClientRect();
    const cx = (e.clientX - rect.left) * (canvas.width / rect.width);
    const cy = (e.clientY - rect.top) * (canvas.height / rect.height);
    const x = Math.round(((cx - C) / UNIT) * 1000) / 1000;
    const y = Math.round(((C - cy) / UNIT) * 1000) / 1000;

    if (x < -3 || x > 3 || y < -5 || y > 3) {
        showError("Точка вне допустимого диапазона (X: -3 … 3, Y: -5 … 3).");
        return;
    }
    window.location.href = window.CONTROLLER_URL + "?x=" + x + "&y=" + y + "&r=" + r;
});

form.addEventListener("submit", (e) => {
    const x = parseNum(xInput.value);
    if (x === null) {
        e.preventDefault();
        showError("X должен быть числом.");
        return;
    }
    if (x < -3 || x > 3) {
        e.preventDefault();
        showError("X должен быть в диапазоне от -3 до 3.");
        return;
    }
    if (yInput.value === "") {
        e.preventDefault();
        showError("Выбери Y.");
        return;
    }
    const r = parseNum(rInput.value);
    if (r === null) {
        e.preventDefault();
        showError("R должен быть числом.");
        return;
    }
    if (r < 1 || r > 4) {
        e.preventDefault();
        showError("R должен быть в диапазоне от 1 до 4.");
        return;
    }
    xInput.value = String(x);
    rInput.value = String(r);
});

redraw();
