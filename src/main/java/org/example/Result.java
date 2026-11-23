package org.example;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Один результат проверки (хранится в HTTP-сессии). */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double x;
    private final double y;
    private final double r;
    private final boolean hit;
    private final String time;
    private final long execMicros;

    public Result(double x, double y, double r, boolean hit, String time, long execMicros) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.time = time;
        this.execMicros = execMicros;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public boolean isHit() { return hit; }
    public String getTime() { return time; }
    public long getExecMicros() { return execMicros; }

    /** Число без экспоненты, максимум 3 знака после точки. */
    public static String fmt(double v) {
        return BigDecimal.valueOf(v).setScale(3, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }
}
