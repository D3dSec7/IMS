package SadSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A simple rounded corner text field to better match the UI mock-ups.
 */
public class RoundedTextField extends JTextField {

    private final int radius;

    public RoundedTextField(String text, int columns) {
        super(text, columns);
        this.radius = 16;
        init();
    }

    public RoundedTextField(int columns) {
        super(columns);
        this.radius = 16;
        init();
    }

    private void init() {
        // Make background transparent so we can paint it ourselves
        setOpaque(false);
        // Some padding so the text is not glued to the borders
        setBorder(new EmptyBorder(8, 12, 8, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(getBackground() != null ? getBackground() : Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);

        // Border
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius * 2, radius * 2);

        g2.dispose();
        // Let the superclass paint the text
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() {
        // We paint the background manually
        return false;
    }
}
