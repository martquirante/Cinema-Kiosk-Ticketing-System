import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Isang custom JPanel na kayang mag-display ng
 * image bilang background na naka-scale to fill.
 */
public class BackgroundImagePanel extends JPanel { 
    private Image backgroundImage;

    public BackgroundImagePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        setLayout(null); // Default to null layout, but we override in KioskMain anyway
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}