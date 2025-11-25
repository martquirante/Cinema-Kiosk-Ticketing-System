import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Isang custom JLabel para sa "G", "PG", "R-13" badges.
 * Awtomatiko itong nagse-set ng kulay at rounded corners,
 * na ngayon ay akma sa provided na icon design.
 */
public class RatingBadge extends JLabel {

    private Color badgeColor;
    private Color borderColor; // Para sa border ng badge

    public RatingBadge(Movie.Rating rating) {
        super(rating.toString()); // Ilagay ang text (e.g., "PG")
        setFont(new Font("Arial", Font.BOLD, 12)); // 
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false); // Gagawin nating transparent ang default background
        
        // Mag-set ng padding sa loob ng badge
        // Inayos ang padding para mas maging compact
        setBorder(new EmptyBorder(3, 8, 3, 8)); 

        // Mag-set ng kulay at border color base sa rating
        switch (rating) {
            case G:
                this.badgeColor = new Color(0, 150, 0); // Mas matingkad na Green
                this.borderColor = new Color(0, 100, 0); // Darker green for border
                break;
            case PG:
                this.badgeColor = new Color(0, 100, 200); // Mas matingkad na Blue
                this.borderColor = new Color(0, 70, 150); // Darker blue for border
                break;
            case R13:
                this.badgeColor = new Color(255, 200, 0); // Matingkad na Yellow-Orange
                this.borderColor = new Color(200, 150, 0); // Darker yellow for border
                break;
            case R16:
                this.badgeColor = new Color(255, 120, 0); // Matingkad na Orange
                this.borderColor = new Color(200, 90, 0); // Darker orange for border
                break;
            case R18:
                this.badgeColor = new Color(220, 0, 0); // Matingkad na Red
                this.borderColor = new Color(170, 0, 0); // Darker red for border
                break;
            default:
                this.badgeColor = Color.GRAY;
                this.borderColor = Color.DARK_GRAY;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Ito ang crucial: Gamitin ang actual size ng component sa pag-draw.
        // Ang getWidth() at getHeight() ay dapat na-set na ng layout manager.
        int width = getWidth();
        int height = getHeight();

        int arc = 10; // 
        int borderThickness = 1; // ❗ Bawasan ang border thickness

        //background color
        g2.setColor(badgeColor);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        // Iguhit ang border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(borderThickness / 2, borderThickness / 2, 
                         width - borderThickness, height - borderThickness, arc, arc);
        
        g2.dispose();
        
        //text sa ibabaw ng background
        super.paintComponent(g);
    }
    
    //Override getPreferredSize para kontrolin ang sukat
    @Override
    public Dimension getPreferredSize() {
        // Kunin ang preferred size ng text + padding
        Dimension textSize = super.getPreferredSize();
        Insets insets = getInsets();
        
        // Calculate the actual preferred width and height needed for the badge
        int preferredWidth = textSize.width + insets.left + insets.right;
        int preferredHeight = textSize.height + insets.top + insets.bottom;
        
        return new Dimension(preferredWidth, preferredHeight);
    }
}