import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    
    // Kulay ng gradient (Pwedeng palitan kung gusto ko ng ibang kulay)
    // Ito ay mula Purple papuntang Dark Blue/Black
    private Color color1 = new Color(80, 0, 180);
    private Color color2 = new Color(20, 20, 30);

    public GradientPanel() {
        setOpaque(false); // Mahalaga ito para gumana ang paintComponent
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Para kuminis ang rendering
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int w = getWidth();
        int h = getHeight();
        
        // Gumagawa ng Vertical Gradient (Top to Bottom)
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        
        super.paintComponent(g);
    }
}