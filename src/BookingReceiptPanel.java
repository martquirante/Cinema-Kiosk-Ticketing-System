import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


public class BookingReceiptPanel extends BackgroundImagePanel {

    private KioskMain mainKiosk;
    
    // Dynamic Labels (Ito yung mga nagbabago ang laman base sa booking data)
    private JLabel lblRefNumber;
    private JLabel lblCustomerName;
    private JLabel lblTicketCount;
    
    // Movie Details
    private JLabel lblMovieTitle;
    private JLabel lblMoviePoster; 
    private JPanel ratingBadgeContainer; 
    
    private JLabel lblShowtime;
    private JLabel lblDate;
    private JLabel lblSeatNumbers;
    private JLabel lblSeatType;
    private JLabel lblPricePerTicket;
    private JLabel lblTotalAmount;
    private JLabel lblPaymentMethod;
    
    // Discount Section
    private JPanel discountPanel;
    private JLabel lblDiscountText;
    
    // The Card to capture (Ito yung mismong "resibo" na itsura na isi-save natin as image)
    private RoundedPanel receiptCard;
    
    // Auto-exit Timer (Para kusa bumalik sa simula pagkatapos ng ilang seconds)
    private Timer autoExitTimer;

    public BookingReceiptPanel(KioskMain mainKiosk) {
        // Null layout sa background pero io-override natin ng GridBagLayout para gitna
        super(null);
        this.mainKiosk = mainKiosk;
        setBackground(new Color(20, 20, 30)); // Dark Background

        setLayout(new GridBagLayout()); // Center the receipt card sa gitna ng screen

        // --- MAIN RECEIPT CARD ---
        // Gumamit ng RoundedPanel para mukhang ticket card talaga
        receiptCard = new RoundedPanel(20, new Color(30, 30, 35));
        receiptCard.setLayout(new BoxLayout(receiptCard, BoxLayout.Y_AXIS));
        receiptCard.setPreferredSize(new Dimension(420, 750)); 
        receiptCard.setMaximumSize(new Dimension(420, 750));
        
        // --- GRADIENT HEADER ---
        // Yung may kulay sa taas ng ticket (Purple/Blue gradient style)
        GradientPanel headerPanel = new GradientPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setPreferredSize(new Dimension(420, 110));
        headerPanel.setMaximumSize(new Dimension(420, 110));
        headerPanel.setBorder(new EmptyBorder(25, 0, 25, 0));

        JLabel lblIcon = new JLabel("🎫"); 
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Booking Confirmed!");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Your tickets are ready");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(230, 230, 230));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblIcon);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        
        receiptCard.add(headerPanel);

        // --- CONTENT CONTAINER ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // REFERENCE NUMBER (Random generated code)
        addLabel(contentPanel, "Booking Reference", 12, Color.GRAY);
        lblRefNumber = new JLabel("XXXXXXXX"); // Placeholder muna, papalitan sa generateReceipt()
        lblRefNumber.setFont(new Font("Arial", Font.BOLD, 32));
        lblRefNumber.setForeground(Color.WHITE);
        lblRefNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblRefNumber);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createSeparator()); // Guhit na pang-hati
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // CUSTOMER & TICKET COUNT ROW
        JPanel row1 = new JPanel(new GridLayout(1, 2));
        row1.setOpaque(false);
        // Tinaasan ang height (100) para magkasya ang vertical list ng names kung marami
        row1.setMaximumSize(new Dimension(400, 100)); 
        
        JPanel pName = createDetailBlock("Customer Names", "Juan Dela Cruz");
        lblCustomerName = (JLabel) pName.getComponent(1); 
        
        JPanel pCount = createDetailBlock("Number of Tickets", "0");
        lblTicketCount = (JLabel) pCount.getComponent(1); 
        
        row1.add(pName);
        row1.add(pCount);
        contentPanel.add(row1);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createSeparator());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // MOVIE DETAILS (With Poster)
        addLabel(contentPanel, "Movie", 12, Color.GRAY);
        
        JPanel movieSectionPanel = new JPanel(new BorderLayout(15, 0));
        movieSectionPanel.setOpaque(false);
        movieSectionPanel.setMaximumSize(new Dimension(400, 80));
        
        // Poster sa kaliwa
        lblMoviePoster = new JLabel();
        lblMoviePoster.setPreferredSize(new Dimension(50, 75)); 
        lblMoviePoster.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        movieSectionPanel.add(lblMoviePoster, BorderLayout.WEST);
        
        // Title at Rating sa kanan
        JPanel rightDetailPanel = new JPanel();
        rightDetailPanel.setLayout(new BoxLayout(rightDetailPanel, BoxLayout.Y_AXIS));
        rightDetailPanel.setOpaque(false);
        
        // Title ng movie na na-purchase
        lblMovieTitle = new JLabel("Movie Title");
        lblMovieTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblMovieTitle.setForeground(Color.WHITE);
        lblMovieTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ratingBadgeContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ratingBadgeContainer.setOpaque(false);
        ratingBadgeContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rightDetailPanel.add(Box.createVerticalGlue()); 
        rightDetailPanel.add(lblMovieTitle);
        rightDetailPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightDetailPanel.add(ratingBadgeContainer);
        rightDetailPanel.add(Box.createVerticalGlue());
        
        movieSectionPanel.add(rightDetailPanel, BorderLayout.CENTER);
        
        contentPanel.add(movieSectionPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // SHOWTIME & DATE
        JPanel row2 = new JPanel(new GridLayout(1, 2));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(400, 45));
        
        JPanel pTime = createDetailBlock("Showtime", "00:00 AM");
        lblShowtime = (JLabel) pTime.getComponent(1);
        
        JPanel pDate = createDetailBlock("Date", "11/22/2025");
        lblDate = (JLabel) pDate.getComponent(1);
        
        row2.add(pTime);
        row2.add(pDate);
        contentPanel.add(row2);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // SEATS (Naka-box para distinct tignan)
        addLabelLeft(contentPanel, "Seat Numbers");
        lblSeatNumbers = new JLabel("A1, A2"); 
        lblSeatNumbers.setFont(new Font("Arial", Font.BOLD, 16));
        lblSeatNumbers.setForeground(Color.WHITE);
        lblSeatNumbers.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setOpaque(false);
        seatPanel.setMaximumSize(new Dimension(400, 40));
        seatPanel.add(lblSeatNumbers, BorderLayout.WEST);
        contentPanel.add(seatPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // TYPE & PRICE
        JPanel row3 = new JPanel(new GridLayout(1, 2));
        row3.setOpaque(false);
        row3.setMaximumSize(new Dimension(400, 45));
        
        JPanel pType = createDetailBlock("Seat Type", "Regular");
        lblSeatType = (JLabel) pType.getComponent(1);
        
        JPanel pPrice = createDetailBlock("Price per Ticket", "₱0");
        lblPricePerTicket = (JLabel) pPrice.getComponent(1);
        
        row3.add(pType);
        row3.add(pPrice);
        contentPanel.add(row3);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // DISCOUNT BOX (Green - Lalabas lang kung may discount)
        discountPanel = new RoundedPanel(10, new Color(20, 60, 30)); 
        discountPanel.setBorder(BorderFactory.createLineBorder(new Color(40, 180, 80), 1));
        discountPanel.setMaximumSize(new Dimension(400, 45));
        discountPanel.setLayout(new GridBagLayout()); 
        
        lblDiscountText = new JLabel("Discount Applied");
        lblDiscountText.setForeground(new Color(100, 255, 120)); 
        lblDiscountText.setFont(new Font("Arial", Font.PLAIN, 13));
        discountPanel.add(lblDiscountText);
        
        discountPanel.setVisible(false); 
        contentPanel.add(discountPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // TOTAL AMOUNT BOX (Purple - Pinaka importante)
        RoundedPanel totalBox = new RoundedPanel(15, new Color(60, 20, 80));
        totalBox.setLayout(new BorderLayout());
        totalBox.setBorder(new EmptyBorder(15, 20, 15, 20));
        totalBox.setMaximumSize(new Dimension(400, 90));
        
        JPanel totalLeft = new JPanel(new GridLayout(2, 1));
        totalLeft.setOpaque(false);
        JLabel lblTotalHeader = new JLabel("Total Amount Paid");
        lblTotalHeader.setForeground(Color.WHITE);
        lblTotalHeader.setFont(new Font("Arial", Font.BOLD, 16));
        
        lblPaymentMethod = new JLabel("Payment Method");
        lblPaymentMethod.setForeground(Color.LIGHT_GRAY);
        lblPaymentMethod.setFont(new Font("Arial", Font.PLAIN, 12));
        
        totalLeft.add(lblTotalHeader);
        totalLeft.add(lblPaymentMethod);
        
        lblTotalAmount = new JLabel("₱0.00");
        lblTotalAmount.setForeground(new Color(200, 150, 255)); 
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 24));
        
        totalBox.add(totalLeft, BorderLayout.WEST);
        totalBox.add(lblTotalAmount, BorderLayout.EAST);
        
        contentPanel.add(totalBox);
        
        // Footer Message (Timer countdown notice)
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        JLabel footer = new JLabel("This screen will close automatically in 10 seconds...");
        footer.setFont(new Font("Arial", Font.ITALIC, 12));
        footer.setForeground(Color.GRAY);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(footer);

        receiptCard.add(contentPanel);
        add(receiptCard);
        
        // CLICK TO EXIT FEATURE (Kung ayaw maghintay ng user ng 10 secs, pwede i-click)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (autoExitTimer != null) autoExitTimer.stop();
                mainKiosk.startNewBooking();
            }
        });
    }

    // --- METHODS ---

    // Ito ang method na tinatawag para punuin ng data ang receipt at i-save ang image
    public void generateReceipt() {
        Booking b = mainKiosk.getCurrentBooking();
        if (b == null) return;

        // Generate Random Reference Number (8 characters)
        String ref = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        lblRefNumber.setText(ref);

        // Vertical Name List gamit ang HTML <br> tag para naka-enter
        List<String> names = b.getCustomerNames();
        if (!names.isEmpty()) {
            // Join names with <br> for newline
            String allNames = String.join("<br>", names);
            // Wrap in HTML tags para gumana ang formatting
            lblCustomerName.setText("<html>" + allNames + "</html>");
        }
        
        lblTicketCount.setText(String.valueOf(b.getNumberOfViewers()));

        // Get Movie Details
        Movie m = b.getSelectedMovie();
        if (m != null) {
            // Nilagyan ng width limit sa HTML para mag-wrap ang text kung mahaba ang title
            lblMovieTitle.setText("<html><div style='width: 200px;'>" + m.getTitle() + "</div></html>");
            ratingBadgeContainer.removeAll();
            ratingBadgeContainer.add(new RatingBadge(m.getRating()));
            
            try {
                ImageIcon icon = new ImageIcon(m.getPosterImagePath());
                Image scaled = icon.getImage().getScaledInstance(50, 75, Image.SCALE_SMOOTH);
                lblMoviePoster.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                lblMoviePoster.setText("No Img"); 
            }

            double price = b.getSeatType().equals("VIP") ? m.getVipPrice() : m.getRegularPrice();
            lblPricePerTicket.setText("₱" + String.format("%.0f", price));
        }

        // Set Date and Time
        lblShowtime.setText(b.getSelectedShowtime());
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        
        // Join selected seats with comma (e.g., "A1, A2")
        lblSeatNumbers.setText(String.join(", ", b.getSelectedSeats()));
        lblSeatType.setText(b.getSeatType());

        // Check Discounts
        if (b.isDiscounted()) {
            discountPanel.setVisible(true);
            lblDiscountText.setText("20% Discount Applied (" + b.getDiscountCount() + " pax)");
        } else {
            discountPanel.setVisible(false);
        }

        // Format Total Amount
        lblTotalAmount.setText("₱" + String.format("%,.2f", b.getTotalAmount()));

        String pMethod = b.getPaymentMethod();
        lblPaymentMethod.setText("Paid via " + (pMethod != null ? pMethod : "Cash"));

        // Force UI refresh bago i-screenshot
        revalidate();
        repaint();

        // I-save ang receipt as Image, pero gawin ito sa ibang thread para di mag-lag
        SwingUtilities.invokeLater(this::saveReceiptImage);
        
        // Start Auto-exit Timer (10 seconds)
        if (autoExitTimer != null && autoExitTimer.isRunning()) {
            autoExitTimer.stop();
        }
        autoExitTimer = new Timer(10000, e -> {
            System.out.println("Timer finished. Returning to home.");
            mainKiosk.startNewBooking();
        });
        autoExitTimer.setRepeats(false); // Isang beses lang tatakbo
        autoExitTimer.start();
    }

    // Method para kuhanan ng "picture" ang panel at i-save as PNG file
    private void saveReceiptImage() {
        try {
            // Gumawa ng folder kung wala pa
            File folder = new File("SOLD_TICKET_FILES");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Filename format: Ticket_REFNUMBER_DATE_TIME.png
            String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "Ticket_" + lblRefNumber.getText() + "_" + timestamp + ".png";
            File file = new File(folder, filename);

            // Gumawa ng empty image na kasing laki ng receipt card
            int w = receiptCard.getWidth();
            int h = receiptCard.getHeight();
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            
            // "Iguhit" ang receipt card papunta sa image (Graphics2D)
            Graphics2D g2d = bi.createGraphics();
            receiptCard.print(g2d); 
            g2d.dispose();

            // I-save ang image sa file gamit ang ImageIO
            ImageIO.write(bi, "png", file);
            System.out.println("Receipt saved: " + file.getAbsolutePath());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // --- UI HELPER METHODS ---

    // Helper para gumawa ng block na may Title (Gray) at Value (White)
    private JPanel createDetailBlock(String title, String value) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);
        // Default to Left Alignment for title
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 14));
        lblValue.setForeground(Color.WHITE);
        // Para kung humaba ang Names (dahil may <br>), ang katabing column (Tickets)
        // ay mananatili sa taas (aligned sa first name) at hindi pupunta sa gitna.
        lblValue.setVerticalAlignment(SwingConstants.TOP);
        lblValue.setHorizontalAlignment(SwingConstants.LEFT); // Ensure left align for text
        
        panel.add(lblTitle);
        panel.add(lblValue);
        return panel;
    }

    private void addLabel(JPanel panel, String text, int size, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
    }

    private void addLabelLeft(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(Color.GRAY);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(400, 20));
        wrapper.add(lbl, BorderLayout.WEST);
        panel.add(wrapper);
    }

    // Helper para sa guhit na pang-hati (Separator)
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(360, 1));
        sep.setForeground(new Color(60, 60, 70));
        return sep;
    }
}