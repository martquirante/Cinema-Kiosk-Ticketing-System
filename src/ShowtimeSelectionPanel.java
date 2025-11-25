import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ShowtimeSelectionPanel extends BackgroundImagePanel { 

    private KioskMain mainKiosk;
    private Movie selectedMovie;
    
    // ! Set to FALSE if tapos na mag-develop. 
    // Taglish: Ginagamit 'to para sa testing. Kapag true, pwede mong piliin kahit nakalipas na oras.
    private final boolean DEBUG_MODE = true; 

    // Data
    private int totalViewers;
    private List<String> selectedSeatsList = new ArrayList<>(); // Listahan ng mga upuan na napili ng user
    private String currentSeatType = "Regular"; 
    private String currentShowtime = null;
    
    // UI Components
    private JPanel timePanel;
    
    // Main container para sa Labels at Buttons ng mga upuan
    private JPanel seatContainer; 
    
    private JLabel seatCountLabel;
    private JLabel priceLabel;
    private JButton nextButton;
    
    private JToggleButton btnRegular;
    private JToggleButton btnVip;
    
    // Fixed na oras ng palabas
    private final List<String> showtimes = Arrays.asList("12:30", "15:00", "18:15", "21:30");
    
    // Colors
    private final Color COLOR_AVAILABLE_REG = new Color(0, 200, 83); 
    private final Color COLOR_AVAILABLE_VIP = new Color(255, 193, 7); 
    private final Color COLOR_SELECTED = new Color(138, 43, 226);     
    private final Color COLOR_OCCUPIED = new Color(60, 63, 65);       
    private final Color COLOR_BG_DARK = new Color(20, 20, 30);

    public ShowtimeSelectionPanel(KioskMain mainKiosk) {
        super(null); 
        setBackground(COLOR_BG_DARK); 
        this.mainKiosk = mainKiosk;

        setLayout(new BorderLayout());
        
        // --- MAIN SCROLL PANE ---
        //Gumawa tayo ng scroll pane para kung sumobra sa taas ang content, pwede i-scroll.
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(20, 30, 20, 30)); 

        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Para mas mabilis ang scroll speed
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);

        // --- HEADER ---
        JLabel title = new JLabel("Select Showtime & Seats");
        title.setFont(new Font("Arial", Font.BOLD, 22)); 
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT); 
        contentContainer.add(title);
        
        priceLabel = new JLabel("Movie Title Placeholder");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        priceLabel.setForeground(Color.LIGHT_GRAY);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        contentContainer.add(priceLabel);
        
        contentContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- TIME SELECTION ---
        JLabel timeHeader = new JLabel("Select Showtime");
        timeHeader.setFont(new Font("Arial", Font.BOLD, 16));
        timeHeader.setForeground(Color.WHITE);
        timeHeader.setAlignmentX(Component.CENTER_ALIGNMENT); 
        contentContainer.add(timeHeader);
        contentContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // Grid layout para sa buttons ng oras (2 columns)
        timePanel = new JPanel(new GridLayout(2, 2, 10, 10)); 
        timePanel.setOpaque(false);
        timePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timePanel.setMaximumSize(new Dimension(320, 100)); 
        contentContainer.add(timePanel);
        
        contentContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- SEAT TYPE ---
        JLabel typeHeader = new JLabel("Select Seat Type");
        typeHeader.setFont(new Font("Arial", Font.BOLD, 16));
        typeHeader.setForeground(Color.WHITE);
        typeHeader.setAlignmentX(Component.CENTER_ALIGNMENT); 
        contentContainer.add(typeHeader);
        contentContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel typePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        typePanel.setOpaque(false);
        typePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        typePanel.setMaximumSize(new Dimension(320, 70)); 

        // Buttons para sa pagpili kung Regular or VIP
        btnRegular = createTypeButton("Regular", "₱350");
        btnVip = createTypeButton("VIP", "₱550");

        // ButtonGroup para isa lang ang pwedeng piliin sa dalawa
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(btnRegular);
        typeGroup.add(btnVip);
        btnRegular.setSelected(true); // Default selection ay Regular

        // Action listeners pag kinlick ang button
        btnRegular.addActionListener(e -> switchSeatMode("Regular"));
        btnVip.addActionListener(e -> switchSeatMode("VIP"));

        typePanel.add(btnRegular);
        typePanel.add(btnVip);
        contentContainer.add(typePanel);

        contentContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- SEAT MAP ---
        seatCountLabel = new JLabel("Select Seats (0/0)");
        seatCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        seatCountLabel.setForeground(Color.WHITE);
        seatCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        contentContainer.add(seatCountLabel);
        
        contentContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // Visual representation ng Screen sa harap
        JLabel screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
        screenLabel.setOpaque(true);
        screenLabel.setBackground(new Color(50, 50, 70));
        screenLabel.setForeground(Color.GRAY);
        screenLabel.setFont(new Font("Arial", Font.BOLD, 12));
        screenLabel.setMaximumSize(new Dimension(300, 25)); 
        screenLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        contentContainer.add(screenLabel);
        
        contentContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Container para sa Labels (A, B, C...) + Grid ng Seats
        seatContainer = new JPanel(new BorderLayout(10, 0)); // May gap na 10px sa pagitan ng Letters at Buttons
        seatContainer.setOpaque(false);
        seatContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentContainer.add(seatContainer);

        // Legend (yung guide kung ano ibig sabihin ng mga kulay)
        contentContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel legendPanel = createLegendPanel();
        legendPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentContainer.add(legendPanel);
        
        contentContainer.add(Box.createRigidArea(new Dimension(0, 30))); 

        // --- NAVIGATION ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton backButton = new JButton("Back");
        styleNavButton(backButton, false);
        backButton.addActionListener(e -> mainKiosk.showMovieSelection(null)); // Balik sa Movie Selection

        nextButton = new JButton("Proceed to Discount");
        styleNavButton(nextButton, true);
        nextButton.setEnabled(false); // Disabled muna hangga't di pa kumpleto ang selection
        nextButton.addActionListener(e -> proceedToNext());

        navPanel.add(backButton);
        navPanel.add(nextButton);
        add(navPanel, BorderLayout.SOUTH);
    }

    // --- LOGIC METHODS ---

    //Ito ang tinatawag pagkalipat sa screen na ito para i-load ang data ng movie
    public void setupPanel(Movie movie) {
        this.selectedMovie = movie;
        this.priceLabel.setText(movie.getTitle());
        
        // Kunin ang bilang ng viewers mula sa booking data
        if (mainKiosk.getCurrentBooking() != null) {
            this.totalViewers = mainKiosk.getCurrentBooking().getNumberOfViewers();
        } else {
            this.totalViewers = 1; 
        }
        
        // Reset:If wala pang naseselect na seat, di pa siya magnenext
        selectedSeatsList.clear();
        currentShowtime = null;
        nextButton.setEnabled(false);
        updateSeatCountLabel();
        
        // Reset: Default selection ay Regular
        btnRegular.setSelected(true);
        switchSeatMode("Regular"); 
        
        // Generate ng buttons para sa oras at update ng presyo
        generateTimeButtons();
        updateButtonPrices();
    }
    
    // Switch mode for VIP & REGULAR
    private void switchSeatMode(String mode) {
        // Kung nagbago ang mode (halimbawa from Regular to VIP),
        // kailangan i-clear ang mga napiling seats kasi mag-iiba ang layout at presyo.
        if (!currentSeatType.equals(mode)) {
            selectedSeatsList.clear();
            updateSeatCountLabel();
            checkProceedCondition();
        }
        this.currentSeatType = mode;
        updateButtonPrices(); 
        generateSeatGrid();   // Gawa ulit ng seat layout
    }

    // Gumagawa ng buttons para sa mga oras (12:30, 15:00, etc.)
    private void generateTimeButtons() {
        timePanel.removeAll();
        ButtonGroup timeGroup = new ButtonGroup();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        boolean firstAvailableSelected = false;

        for (String time : showtimes) {
            JToggleButton btn = new JToggleButton(convertTimeFormat(time));
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            
            LocalTime showTimeObj = LocalTime.parse(time, formatter);
            
            // Kung hindi DEBUG_MODE at lumipas na ang oras, i-disable ang button.
            if (!DEBUG_MODE && currentTime.isAfter(showTimeObj)) {
                btn.setEnabled(false);
                btn.setBackground(new Color(40, 40, 40));
                btn.setForeground(Color.GRAY);
            } else {
                // Kung available pa, pwede i-click
                btn.setBackground(new Color(60, 55, 90));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    currentShowtime = btn.getText();
                    checkProceedCondition();
                    resetTimeButtonColors(timePanel);
                    btn.setBackground(new Color(160, 130, 255)); // Highlight pag pinili
                });
                timeGroup.add(btn);
                // Auto-select sa unang available na oras
                if (!firstAvailableSelected) {
                    btn.doClick();
                    firstAvailableSelected = true;
                }
            }
            timePanel.add(btn);
        }
        timePanel.revalidate();
        timePanel.repaint();
    }

    // Reset color ng time buttons para yung selected lang ang ma-highlight
    private void resetTimeButtonColors(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JToggleButton && c.isEnabled()) {
                c.setBackground(new Color(60, 55, 90));
            }
        }
    }

    // Taglish: Dito ginagawa ang layout ng mga upuan (may rows A, B, C...)
    private void generateSeatGrid() {
        seatContainer.removeAll(); // Clear container muna
        Random random = new Random();
        
        JPanel labelsPanel;
        JPanel buttonsPanel;
        
        char[] rows;
        int colsCount;
        int gap;
        
        // Iba ang layout pag VIP (konti lang upuan) vs Regular (marami)
        if (currentSeatType.equals("VIP")) {
            // VIP Setup
            rows = new char[]{'A', 'B', 'C', 'D'};
            colsCount = 4;
            gap = 15;
            seatContainer.setMaximumSize(new Dimension(340, 220));
        } else {
            // Regular Setup
            rows = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
            colsCount = 8;
            gap = 5;
            seatContainer.setMaximumSize(new Dimension(360, 320));
        }

        // Create Label Column (Left part: A, B, C...)
        // GridLayout(rows, 1) para vertical list
        labelsPanel = new JPanel(new GridLayout(rows.length, 1, 0, gap)); 
        labelsPanel.setOpaque(false);
        
        for (char row : rows) {
            JLabel lbl = new JLabel(String.valueOf(row), SwingConstants.CENTER);
            lbl.setForeground(Color.GRAY);
            lbl.setFont(new Font("Arial", Font.BOLD, 14));
            labelsPanel.add(lbl);
        }

        // Create Buttons Grid (Right part: 1, 2, 3...)
        buttonsPanel = new JPanel(new GridLayout(rows.length, colsCount, gap, gap));
        buttonsPanel.setOpaque(false);

        for (char row : rows) {
            for (int col = 1; col <= colsCount; col++) {
                createSeatButton(buttonsPanel, row, col, random, currentSeatType.equals("VIP"));
            }
        }

        // Add both to main container
        seatContainer.add(labelsPanel, BorderLayout.WEST);
        seatContainer.add(buttonsPanel, BorderLayout.CENTER);

        seatContainer.revalidate();
        seatContainer.repaint();
    }
    
   // Helper method para gumawa ng isang seat button
    private void createSeatButton(JPanel parent, char row, int col, Random random, boolean isVip) {
        String seatId = row + String.valueOf(col); // Example ID: "A1"
        JButton seatBtn = new JButton(String.valueOf(col));
        
        // Mas malaki ang button pag VIP
        if (isVip) {
            seatBtn.setPreferredSize(new Dimension(50, 40)); 
            seatBtn.setFont(new Font("Arial", Font.BOLD, 14));
        } else {
            seatBtn.setPreferredSize(new Dimension(32, 32)); 
            seatBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        }
        
        seatBtn.setMargin(new Insets(0,0,0,0));
        seatBtn.setFocusPainted(false);
        seatBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Taglish: Random logic para kunwari may nakaupo na (20% chance na occupied)
        boolean isOccupied = random.nextInt(100) < 20; 
        
        if (isOccupied) {
            seatBtn.setBackground(COLOR_OCCUPIED);
            seatBtn.setForeground(Color.GRAY);
            seatBtn.setEnabled(false); // Di pwede i-click
        } else {
            seatBtn.setBackground(isVip ? COLOR_AVAILABLE_VIP : COLOR_AVAILABLE_REG);
            seatBtn.setForeground(isVip ? Color.BLACK : Color.WHITE); 
            seatBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Add action listener pag kinlick
            seatBtn.addActionListener(e -> handleSeatClick(seatBtn, seatId, isVip));
        }
        parent.add(seatBtn); // Add directly to button panel
    }

    // Taglish: Naghahandle ng logic kapag kinlick ang upuan
    private void handleSeatClick(JButton btn, String seatId, boolean isVip) {
        Color defaultColor = isVip ? COLOR_AVAILABLE_VIP : COLOR_AVAILABLE_REG;
        Color textColor = isVip ? Color.BLACK : Color.WHITE;
        
        // Kung nasa listahan na (naka-select na), i-unselect (remove)
        if (selectedSeatsList.contains(seatId)) {
            selectedSeatsList.remove(seatId);
            btn.setBackground(defaultColor); // Balik sa dating kulay
            btn.setForeground(textColor);
        } else {
            // Kung hindi pa puno ang seats, i-add sa listahan
            if (selectedSeatsList.size() < totalViewers) {
                selectedSeatsList.add(seatId);
                btn.setBackground(COLOR_SELECTED); // Gawing purple
                btn.setForeground(Color.WHITE);
            } else {
                // Kung sobra na sa required viewers, magpakita ng error
                JOptionPane.showMessageDialog(this, "You have already selected " + totalViewers + " seats.");
            }
        }
        updateSeatCountLabel();
        checkProceedCondition(); // Check kung pwede na mag-Next
    }

    private void updateSeatCountLabel() {
        seatCountLabel.setText("Select Seats (" + selectedSeatsList.size() + "/" + totalViewers + ")");
    }

    // Taglish: Update ng presyo sa buttons depende sa movie at seat type
    private void updateButtonPrices() {
        if (selectedMovie == null) return;
        
        // HTML formatting para maganda tignan ang presyo sa loob ng button
        String regText = "<html><center><font size='4'>Regular</font><br><font size='4' color='yellow'>₱" + String.format("%.0f", selectedMovie.getRegularPrice()) + "</font></center></html>";
        String vipText = "<html><center><font size='4'>VIP</font><br><font size='4' color='yellow'>₱" + String.format("%.0f", selectedMovie.getVipPrice()) + "</font></center></html>";
        
        btnRegular.setText(regText);
        btnVip.setText(vipText);
        
        // Lagyan ng border highlight kung alin ang active na mode
        if (currentSeatType.equals("Regular")) {
            btnRegular.setBorder(BorderFactory.createLineBorder(new Color(160, 130, 255), 3));
            btnVip.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        } else {
            btnVip.setBorder(BorderFactory.createLineBorder(new Color(160, 130, 255), 3));
            btnRegular.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        }
    }

    // Taglish: Che-check kung pwede na i-enable ang Next button
    // Dapat kumpleto ang bilang ng upuan at may napiling oras
    private void checkProceedCondition() {
        boolean isSeatCountComplete = selectedSeatsList.size() == totalViewers;
        boolean isTimeSelected = currentShowtime != null;
        nextButton.setEnabled(isSeatCountComplete && isTimeSelected);
    }

    // Taglish: Save ang data sa booking object at lipat sa Discount screen
    private void proceedToNext() {
        Booking booking = mainKiosk.getCurrentBooking();
        booking.setSelectedShowtime(currentShowtime);
        booking.setSeatType(currentSeatType);
        
        // Clear muna lumang data bago i-save ang bago
        booking.getSelectedSeats().clear();
        for (String seat : selectedSeatsList) {
            booking.addSelectedSeat(seat);
        }
        mainKiosk.showDiscountEligibility(totalViewers);
    }

    // --- HELPER UI METHODS ---
    // Convert 24-hour format (13:00) to 12-hour format (1:00 PM)
    private String convertTimeFormat(String time24) {
        try {
            LocalTime t = LocalTime.parse(time24);
            return t.format(DateTimeFormatter.ofPattern("h:mm a"));
        } catch (Exception e) { return time24; }
    }

    private JToggleButton createTypeButton(String type, String priceText) {
        JToggleButton btn = new JToggleButton();
        btn.setBackground(new Color(40, 35, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        return btn;
    }

    // Gawa ng legend sa baba para alam ng user ang meaning ng kulay
    private JPanel createLegendPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); 
        p.setOpaque(false);
        p.add(createLegendItem(COLOR_AVAILABLE_REG, "Regular"));
        p.add(createLegendItem(COLOR_AVAILABLE_VIP, "VIP"));
        p.add(createLegendItem(COLOR_SELECTED, "Selected"));
        p.add(createLegendItem(COLOR_OCCUPIED, "Occupied"));
        return p;
    }

    private JPanel createLegendItem(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(12, 12));
        box.setBackground(c);
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        p.add(box);
        p.add(lbl);
        return p;
    }
    
    // Helper para sa styling ng Back at Next buttons
    private void styleNavButton(JButton btn, boolean isPrimary) {
        btn.setFont(new Font("Arial", Font.BOLD, 14)); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 40)); 
        if (isPrimary) {
            btn.setBackground(new Color(103, 58, 183)); 
        } else {
            btn.setBackground(new Color(60, 63, 65));
        }
    }
}