import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;

public class GraphPanel extends JPanel {
    private final String expression;
    private final double minX;
    private final double maxX;
    private final double step; // user supplied step

    // dynamic y-range computed at paint time
    private double minY = -10;
    private double maxY = 10;

    public GraphPanel(String expression, double minX, double maxX, double step) {
        this.expression = expression;
        this.minX = minX;
        this.maxX = maxX;
        this.step = (step > 0) ? step : 0.01;
    }

    // Convert mathematical X to pixel X (guard against zero-width)
    private int toPixelX(double x) {
        double spanX = maxX - minX;
        if (spanX == 0) return getWidth() / 2;
        double frac = (x - minX) / spanX;
        return (int) Math.round(frac * getWidth());
    }

    // Convert mathematical Y to pixel Y (inverted; guard against zero-height)
    private int toPixelY(double y) {
        double spanY = maxY - minY;
        if (spanY == 0) return getHeight() / 2;
        double frac = (maxY - y) / spanY;
        return (int) Math.round(frac * getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        computeYRange();  
        drawGrid(g2);
        drawAxes(g2);
        drawFunction(g2);
    }

    // Sample function values from minX..maxX to choose minY/maxY
    private void computeYRange() {
        double localMin = Double.POSITIVE_INFINITY;
        double localMax = Double.NEGATIVE_INFINITY;

        // number of samples capped to avoid locking UI on tiny step
        int maxSamples = 100_000;
        int samples = (int) Math.ceil((maxX - minX) / step);
        samples = Math.max(10, Math.min(maxSamples, samples));

        for (int i = 0; i <= samples; i++) {
            double x = minX + i * ( (maxX - minX) / (double) samples );
            if (x > maxX) x = maxX;
            double y = Functions.evaluate(expression, x);

            if (Double.isFinite(y)) {
                if (y < localMin) localMin = y;
                if (y > localMax) localMax = y;
            }
        }

        if (!Double.isFinite(localMin)) localMin = -10;
        if (!Double.isFinite(localMax)) localMax = 10;
        if (localMin == localMax) { // constant function
            localMin -= 1;
            localMax += 1;
        }

        // add small padding
        double pad = (localMax - localMin) * 0.1;
        if (pad == 0) pad = 1;
        minY = localMin - pad;
        maxY = localMax + pad;
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(200, 200, 200)); // light gray

        // vertical integer grid lines
        int startX = (int) Math.ceil(minX);
        int endX = (int) Math.floor(maxX);
        for (int xi = startX; xi <= endX; xi++) {
            int px = toPixelX(xi);
            g2.drawLine(px, 0, px, getHeight());
        }

        // horizontal integer grid lines
        int startY = (int) Math.ceil(minY);
        int endY = (int) Math.floor(maxY);
        for (int yi = startY; yi <= endY; yi++) {
            int py = toPixelY(yi);
            g2.drawLine(0, py, getWidth(), py);
        }
    }

    private void drawAxes(Graphics2D g2) {
        g2.setColor(Color.GRAY);

        int zeroX = toPixelX(0);
        int zeroY = toPixelY(0);

        // X axis (only if inside panel)
        if (zeroY >= 0 && zeroY <= getHeight()) {
            g2.drawLine(0, zeroY, getWidth(), zeroY);
        }

        // Y axis (only if inside panel)
        if (zeroX >= 0 && zeroX <= getWidth()) {
            g2.drawLine(zeroX, 0, zeroX, getHeight());
        }
    }

    private void drawFunction(Graphics2D g2) {
        g2.setColor(Color.BLUE);

        // Use computed samples to draw, avoids repeated evaluation at slightly different x due to floating error
        int maxSamples = 100_000_000;
        int samples = (int) Math.ceil((maxX - minX) / step);
        samples = Math.max(10, Math.min(maxSamples, samples));

        Double prevX = null;
        Double prevY = null;

        for (int i = 0; i <= samples; i++) {
            double x = minX + i * ( (maxX - minX) / (double) samples );
            if (x > maxX) x = maxX;
            double y = Functions.evaluate(expression, x);

            if (!Double.isFinite(y)) {
                prevX = null;
                prevY = null;
                continue;
            }

            if (prevX != null && prevY != null) {
                g2.drawLine(toPixelX(prevX), toPixelY(prevY), toPixelX(x), toPixelY(y));
            }

            prevX = x;
            prevY = y;
        }
    }
}