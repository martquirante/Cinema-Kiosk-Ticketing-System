import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MovieSelectionPanel extends RoundedPanel { 

    private KioskMain mainKiosk;
    private JPanel movieListPanel; 
    private JLabel subTitle;
    private JButton nextButton;

    private Movie selectedMovie = null;
    private JPanel previouslySelectedMoviePanel = null; 

    private Border defaultMovieBorder;
    private Border selectedMovieBorder;

    public MovieSelectionPanel(KioskMain mainKiosk) {
        //Tawagin ang constructor ng RoundedPanel (ang background color niya)
        super(30, new Color(30, 30, 30, 200)); 
        
        this.mainKiosk = mainKiosk;

        //Custom Borders para sa Movie Panels
        defaultMovieBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 55, 90), 2), 
            new EmptyBorder(10, 10, 10, 10) // Padding sa loob
        );
        selectedMovieBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 130, 255), 3), 
            new EmptyBorder(10, 10, 10, 10)
        );


        // Gagamitin na ang BorderLayout direkta sa MovieSelectionPanel (na isa nang RoundedPanel).
        setLayout(new BorderLayout(0, 10)); // BorderLayout na may vertical gap
        setBorder(new EmptyBorder(25, 25, 25, 25)); // Padding sa loob ng RoundedPanel

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Select Your Movie");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        subTitle = new JLabel("Loading available movies...");
        subTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subTitle.setForeground(Color.LIGHT_GRAY);
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(subTitle);
        add(headerPanel, BorderLayout.NORTH); // ❗ Idagdag direkta sa MovieSelectionPanel

        // --- Movie List Panel ---
        movieListPanel = new JPanel();
        movieListPanel.setLayout(new BoxLayout(movieListPanel, BoxLayout.Y_AXIS)); 
        movieListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(movieListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER); // ❗ Idagdag direkta sa MovieSelectionPanel

        // --- Navigation (Back/Next) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton backButton = new JButton("Back");
        nextButton = new JButton("Next");
        nextButton.setEnabled(false); 

        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH); // ❗ Idagdag direkta sa MovieSelectionPanel

        // ❗ 6. Inalis ang add(contentPanel) dahil ito na mismo ang contentPanel.

        // --- Action Listeners ---
        backButton.addActionListener(e -> mainKiosk.showAgeVerificationPanel());
        nextButton.addActionListener(e -> {
            if (selectedMovie != null) {
                mainKiosk.showShowtimeSelection(selectedMovie);
            }
        });
    }

    public void filterAndDisplayMovies(List<Integer> ages, List<Movie> allMovies) {
        movieListPanel.removeAll();
        selectedMovie = null;
        previouslySelectedMoviePanel = null;
        nextButton.setEnabled(false);

        List<Movie> availableMovies = new ArrayList<>();
        int minimumRequiredAge = 0;
        if (!ages.isEmpty()) {
            minimumRequiredAge = ages.stream().mapToInt(Integer::intValue).min().orElse(0);
        }

        for (Movie movie : allMovies) {
            if (movie.isAgeLegal(ages)) {
                availableMovies.add(movie);
            }
        }

        if (availableMovies.isEmpty()) {
            subTitle.setText("No movies available for your group's age(s).");
        } else {
            subTitle.setText("Showing " + availableMovies.size() + " movie(s).");
            for (Movie movie : availableMovies) {
                movieListPanel.add(createMovieListItem(movie)); 
                movieListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        movieListPanel.revalidate();
        movieListPanel.repaint();
    }

        /**
     * ❗ INAYOS ANG SIZING DITO
     */
    private JPanel createMovieListItem(Movie movie) {
        RoundedPanel itemPanel = new RoundedPanel(15, new Color(40, 35, 70));
        itemPanel.setLayout(new BorderLayout(10, 0)); 
        itemPanel.setBorder(defaultMovieBorder);
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); 
        itemPanel.setPreferredSize(new Dimension(300, 150)); 

        // --- Poster (West) ---
        try {
            ImageIcon originalIcon = new ImageIcon(movie.getPosterImagePath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(90, 135, Image.SCALE_SMOOTH); 
            JLabel posterLabel = new JLabel(new ImageIcon(scaledImage));
            posterLabel.setBorder(new EmptyBorder(0, 5, 0, 0)); 
            itemPanel.add(posterLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("Error loading poster: " + movie.getPosterImagePath());
            itemPanel.add(new JLabel("No Poster"), BorderLayout.WEST);
        }

        // --- Info Panel (Center) ---
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(0, 0, 0, 5)); 
        GridBagConstraints gbc = new GridBagConstraints();
        
        // ❗ DITO NAGSIMULA ANG PAG-AYOS
        gbc.anchor = GridBagConstraints.NORTHWEST; // Default anchor

        // Row 0: Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Hayaan ang Title na kunin ang extra space
        gbc.fill = GridBagConstraints.HORIZONTAL; // Hayaan ang Title na mag-stretch
        JLabel titleLabel = new JLabel("<html><b>" + movie.getTitle() + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); 
        titleLabel.setForeground(Color.WHITE);
        infoPanel.add(titleLabel, gbc);

        // Row 0: Rating Badge
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0; // Wag palakihin ang Badge
        gbc.fill = GridBagConstraints.NONE; // ❗ ITO ANG FIX: Wag i-stretch ang Badge
        gbc.anchor = GridBagConstraints.NORTHEAST; // Idikit sa top-right
        infoPanel.add(new RatingBadge(movie.getRating()), gbc); 

        // Row 1: "Regular"
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; // Wag i-stretch
        gbc.insets = new Insets(10, 0, 0, 0); 
        gbc.anchor = GridBagConstraints.SOUTHWEST; 
        gbc.weighty = 1.0; // Itulak pababa
        JLabel regLabel = new JLabel("Regular");
        regLabel.setFont(new Font("Arial", Font.PLAIN, 14)); 
        regLabel.setForeground(Color.LIGHT_GRAY);
        infoPanel.add(regLabel, gbc);

        // Row 1: "VIP"
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; // Wag i-stretch
        gbc.anchor = GridBagConstraints.SOUTHEAST; 
        JLabel vipLabel = new JLabel("VIP");
        vipLabel.setFont(new Font("Arial", Font.PLAIN, 14)); 
        vipLabel.setForeground(Color.LIGHT_GRAY);
        infoPanel.add(vipLabel, gbc);

        // Row 2: Regular Price
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; // Wag i-stretch
        gbc.insets = new Insets(0, 0, 0, 0); 
        gbc.anchor = GridBagConstraints.NORTHWEST; 
        gbc.weighty = 0; // Reset weighty
        JLabel regPriceLabel = new JLabel("₱" + String.format("%.0f", movie.getRegularPrice()));
        regPriceLabel.setFont(new Font("Arial", Font.BOLD, 16)); 
        regPriceLabel.setForeground(Color.WHITE);
        infoPanel.add(regPriceLabel, gbc);

        // Row 2: VIP Price
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; // Wag i-stretch
        gbc.anchor = GridBagConstraints.NORTHEAST; 
        JLabel vipPriceLabel = new JLabel("₱" + String.format("%.0f", movie.getVipPrice()));
        vipPriceLabel.setFont(new Font("Arial", Font.BOLD, 16)); 
        vipPriceLabel.setForeground(Color.WHITE);
        infoPanel.add(vipPriceLabel, gbc);

        itemPanel.add(infoPanel, BorderLayout.CENTER);

        // --- Mouse Listener for Selection ---
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (previouslySelectedMoviePanel != null) {
                    ((RoundedPanel)previouslySelectedMoviePanel).setBackground(new Color(40, 35, 70));
                    previouslySelectedMoviePanel.setBorder(defaultMovieBorder);
                }
                
                itemPanel.setBackground(new Color(103, 58, 183)); // Purple highlight
                itemPanel.setBorder(selectedMovieBorder);
                
                previouslySelectedMoviePanel = itemPanel;
                selectedMovie = movie;
                nextButton.setEnabled(true);
                System.out.println("Selected movie: " + movie.getTitle());
            }
        });

        return itemPanel;
    }
    }
