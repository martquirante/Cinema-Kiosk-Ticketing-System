import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AttractScreenPanel extends JPanel {
    //para tawagin si Mainkiosk's dahil nasa utak o siya ung boss
    private final KioskMain mainKiosk; 
    private List<Movie> movies;
    private Timer slideshowTimer;
    private int currentMovieIndex = 0;
    
    private JLabel posterLabel; 
    
    public AttractScreenPanel(KioskMain mainKiosk, List<Movie> movies) {
        this.mainKiosk = mainKiosk;
        this.movies = movies;
        
        setBackground(new Color(30, 25, 60)); // Ung parang violet or blue basta nakuha ko lang yan RGB ba web
        setLayout(new BorderLayout());
        
        posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(posterLabel, BorderLayout.CENTER);
        
        //Timer ng Slide show
        // 3000 is eqievalent = 3 seconds
        slideshowTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextMovie();
            }
        });
        //Ito ung MouseListener
        //If na click na ung is mapupunta siya sa Welcome Panel
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainKiosk.startBooking();
            }
        });
        //JLabel for "Click Anyware to Buy Ticket!"
        JLabel clickLabel = new JLabel("Click Anyware to Buy Ticket!");
        clickLabel.setFont(new Font("Arial", Font.BOLD, 20));
        clickLabel.setForeground(Color.WHITE);
        clickLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //Ito ung space sa baba 50 ung bottum                 T L  B  R
        clickLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0)); 
        add(clickLabel, BorderLayout.SOUTH);
    }
    // sownext movie if walang poster is may text na lalabas
    private void showNextMovie() {
        if (movies == null || movies.isEmpty()) {
            posterLabel.setText("No movies to display.");
            posterLabel.setForeground(Color.WHITE);
            return;
        }
        
        /**
         * Init ng mga image na movvie 
         **/
        Movie movie = movies.get(currentMovieIndex);
        
        ImageIcon posterIcon = new ImageIcon(movie.getPosterImagePath());
        Image scaledImage = scaleImage(posterIcon.getImage(), 400, 600); 
        posterLabel.setIcon(new ImageIcon(scaledImage));
        
        currentMovieIndex = (currentMovieIndex + 1) % movies.size();
    }
    
    /**
     *For sommother ung pag next slide ng mga movie poster
     * Ang scaleImage method na 'yan ay ang trabaho niya ay liitan o paliitin ang
     * isang imahe para magkasya sa isang target na sukat (maxWidth, maxHeight),
     * nang hindi nababago ang proportion (aspect ratio) niya
     **/
    private Image scaleImage(Image img, int maxWidth, int maxHeight) {
        int originalWidth = img.getWidth(null);
        int originalHeight = img.getHeight(null);
        
        if (originalWidth == -1 || originalHeight == -1) {
            return img.getScaledInstance(maxWidth, maxHeight, Image.SCALE_SMOOTH);
        }
        
        if (originalWidth > originalHeight) {
            int newHeight = (originalHeight * maxWidth) / originalWidth;
            if (newHeight <= 0) newHeight = maxHeight; 
            return img.getScaledInstance(maxWidth, newHeight, Image.SCALE_SMOOTH);
        } else {
            int newWidth = (originalWidth * maxHeight) / originalHeight;
            if (newWidth <= 0) newWidth = maxWidth; 
            return img.getScaledInstance(newWidth, maxHeight, Image.SCALE_SMOOTH);
        }
    }
   //If napa-run na is mag-start ung na mag show ung Panel na toh
    public void startSlideshow() {
        showNextMovie(); 
        slideshowTimer.start();
    }
    //If na-click na ung mouse
    public void stopSlideshow() {
        slideshowTimer.stop();
    }
}