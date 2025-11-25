import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;

public class PaymentPanel extends BackgroundImagePanel {

    private KioskMain mainKiosk;
    
    // Data
    private double totalAmountToPay = 0.0;
    private boolean isCashSelected = true; 

    // UI Components
    private JLabel totalAmountLabel;
    private JLabel ticketPriceLabel;
    private JPanel dynamicPaymentArea; 
    
    // Cash Receipt Components
    private JLabel receiptTotalLabel;
    
    // GCash Components
    private JLabel qrCodeLabel;

    // Buttons
    private JButton btnCash;
    private JButton btnGcash;
    private JButton completeButton;

    // Colors
    private final Color COLOR_BG_DARK = new Color(20, 20, 30);
    private final Color COLOR_PURPLE_CARD = new Color(75, 0, 130);
    private final Color COLOR_CASH_GREEN = new Color(0, 200, 83);
    private final Color COLOR_GCASH_BLUE = new Color(0, 114, 245);
    private final Color COLOR_HOME_BLUE = new Color(0, 150, 200); 
    private final Color COLOR_RECEIPT_BG = new Color(15, 15, 20); 

    public PaymentPanel(KioskMain mainKiosk) {
        super(null); 
        setBackground(COLOR_BG_DARK);
        this.mainKiosk = mainKiosk;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // --- HEADER ---
        addLabel("Payment", new Font("Arial", Font.BOLD, 28), Color.WHITE);
        add(Box.createRigidArea(new Dimension(0, 5)));
        addLabel("Choose your payment method", new Font("Arial", Font.PLAIN, 14), Color.GRAY);
        add(Box.createRigidArea(new Dimension(0, 15)));

        // --- SUMMARY CARD (PURPLE) ---
        JPanel summaryCard = new RoundedPanel(20, COLOR_PURPLE_CARD);
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setMaximumSize(new Dimension(400, 120)); 
        summaryCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        summaryCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTotalText = new JLabel("Total Amount to Pay");
        lblTotalText.setForeground(new Color(220, 220, 220));
        lblTotalText.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTotalText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        totalAmountLabel = new JLabel("₱0.00");
        totalAmountLabel.setForeground(Color.WHITE);
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 32));
        totalAmountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(350, 5));
        separator.setForeground(new Color(150, 50, 200));

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);
        detailsPanel.setMaximumSize(new Dimension(350, 20));
        JLabel lblTickets = new JLabel("Tickets:");
        lblTickets.setForeground(Color.LIGHT_GRAY);
        ticketPriceLabel = new JLabel("₱0.00");
        ticketPriceLabel.setForeground(Color.WHITE);
        detailsPanel.add(lblTickets, BorderLayout.WEST);
        detailsPanel.add(ticketPriceLabel, BorderLayout.EAST);

        summaryCard.add(lblTotalText);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 5)));
        summaryCard.add(totalAmountLabel);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 10)));
        summaryCard.add(separator);
        summaryCard.add(Box.createRigidArea(new Dimension(0, 10)));
        summaryCard.add(detailsPanel);

        add(summaryCard);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // --- PAYMENT METHOD SELECTORS ---
        JPanel methodPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        methodPanel.setOpaque(false);
        methodPanel.setMaximumSize(new Dimension(400, 50)); 
        methodPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCash = createMethodButton("Cash", COLOR_CASH_GREEN);
        btnGcash = createMethodButton("GCash", COLOR_GCASH_BLUE);

        btnCash.addActionListener(e -> selectPaymentMethod(true));
        btnGcash.addActionListener(e -> selectPaymentMethod(false));

        methodPanel.add(btnCash);
        methodPanel.add(btnGcash);
        add(methodPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // --- DYNAMIC AREA ---
        dynamicPaymentArea = new RoundedPanel(20, new Color(30, 35, 45)); 
        dynamicPaymentArea.setLayout(new CardLayout()); 
        dynamicPaymentArea.setMaximumSize(new Dimension(400, 320)); 
        dynamicPaymentArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- CASH UI ---
        JPanel cashView = new JPanel();
        cashView.setOpaque(false);
        cashView.setLayout(new BoxLayout(cashView, BoxLayout.Y_AXIS));
        cashView.setBorder(new EmptyBorder(30, 20, 20, 20)); 

        JLabel lblCashInst = new JLabel("Insert your payment into the kiosk machine.");
        lblCashInst.setFont(new Font("Arial", Font.BOLD, 18));
        lblCashInst.setForeground(Color.WHITE);
        lblCashInst.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cashView.add(lblCashInst);
        cashView.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // RECEIPT STYLE BOX
        JPanel receiptBox = new RoundedPanel(15, COLOR_RECEIPT_BG);
        receiptBox.setLayout(new BorderLayout());
        receiptBox.setMaximumSize(new Dimension(300, 120));
        receiptBox.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Receipt Content
        JPanel receiptContent = new JPanel(new GridLayout(3, 1, 0, 5));
        receiptContent.setOpaque(false);
        
        receiptContent.add(createReceiptRow("Amount Given:", "Counter", Color.GRAY));
        receiptTotalLabel = new JLabel("₱0.00", SwingConstants.RIGHT); 
        receiptContent.add(createReceiptRowCustom("Total:", receiptTotalLabel));
        
        // Green Change Row
        JPanel changeRow = new JPanel(new BorderLayout());
        changeRow.setOpaque(false);
        JLabel lblChangeTitle = new JLabel("Change:");
        lblChangeTitle.setForeground(Color.GREEN);
        lblChangeTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel lblChangeValue = new JLabel("Counter");
        lblChangeValue.setForeground(Color.GREEN);
        lblChangeValue.setFont(new Font("Arial", Font.BOLD, 14));
        changeRow.add(lblChangeTitle, BorderLayout.WEST);
        changeRow.add(lblChangeValue, BorderLayout.EAST);
        
        // Line Separator
        JSeparator receiptSep = new JSeparator();
        receiptSep.setForeground(Color.DARK_GRAY);
        
        receiptBox.add(receiptContent, BorderLayout.CENTER);
        receiptBox.add(changeRow, BorderLayout.SOUTH);
        
        cashView.add(receiptBox);

        // --- VIEW 2: GCASH QR ---
        JPanel gcashView = new JPanel();
        gcashView.setOpaque(false);
        gcashView.setLayout(new BoxLayout(gcashView, BoxLayout.Y_AXIS));
        gcashView.setBorder(new EmptyBorder(20, 10, 10, 10));
        
        JLabel lblScan = new JLabel("Scan QR Code to Pay");
        lblScan.setForeground(Color.LIGHT_GRAY);
        lblScan.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        qrCodeLabel = new JLabel(); 
        qrCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrCodeLabel.setBorder(BorderFactory.createLineBorder(COLOR_GCASH_BLUE, 2));

        gcashView.add(lblScan);
        gcashView.add(Box.createRigidArea(new Dimension(0, 10)));
        gcashView.add(qrCodeLabel);

        dynamicPaymentArea.add(cashView, "CASH");
        dynamicPaymentArea.add(gcashView, "GCASH");

        add(dynamicPaymentArea);
        add(Box.createVerticalGlue()); 

        // --- NAVIGATION BUTTONS ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setOpaque(false);
        navPanel.setMaximumSize(new Dimension(500, 60));

        JButton btnBack = new JButton("Back");
        styleButton(btnBack, Color.WHITE, Color.BLACK);
        btnBack.addActionListener(e -> mainKiosk.showDiscountEligibility(mainKiosk.getCurrentBooking().getNumberOfViewers())); 

        // HOME BUTTON
        JButton btnHome = new JButton("Home Page");
        styleButton(btnHome, COLOR_HOME_BLUE, Color.WHITE);
        btnHome.addActionListener(e -> handleHomeAction());
        
        // COMPLETE BOOKING BUTTON
        completeButton = new JButton("Complete Booking");
        styleButton(completeButton, new Color(20, 200, 83), Color.WHITE); 
        completeButton.addActionListener(e -> handleCompleteBooking());

        navPanel.add(btnBack);
        navPanel.add(btnHome);
        navPanel.add(completeButton); 

        add(navPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
    }

    // --- METHODS ---

    public void setupPanel() {
        if (mainKiosk.getCurrentBooking() != null) {
            this.totalAmountToPay = mainKiosk.getCurrentBooking().getTotalAmount();
        } else {
            this.totalAmountToPay = 0;
        }
        
        loadQrImage();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formattedPrice = "₱" + df.format(totalAmountToPay);
        
        totalAmountLabel.setText(formattedPrice);
        ticketPriceLabel.setText(formattedPrice);
        
        receiptTotalLabel.setText(formattedPrice);
        receiptTotalLabel.setForeground(Color.WHITE);
        receiptTotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        selectPaymentMethod(true);
    }
    
    private void loadQrImage() {
        String path = "images/qr_code.png";
        File imgFile = new File(path);
        
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(230, 230, Image.SCALE_SMOOTH);
            qrCodeLabel.setIcon(new ImageIcon(scaled));
            qrCodeLabel.setText(""); 
        } else {
            qrCodeLabel.setIcon(null);
            qrCodeLabel.setText("QR Code Not Found");
            qrCodeLabel.setForeground(Color.RED);
        }
    }

    private void selectPaymentMethod(boolean isCash) {
        this.isCashSelected = isCash;
        CardLayout cl = (CardLayout) dynamicPaymentArea.getLayout();

        if (isCash) {
            cl.show(dynamicPaymentArea, "CASH");
            btnCash.setBorder(BorderFactory.createLineBorder(COLOR_CASH_GREEN, 2));
            btnGcash.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else {
            cl.show(dynamicPaymentArea, "GCASH");
            btnGcash.setBorder(BorderFactory.createLineBorder(COLOR_GCASH_BLUE, 2));
            btnCash.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    private JPanel createReceiptRow(String label, String value, Color valColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel v = new JLabel(value);
        v.setForeground(valColor);
        v.setFont(new Font("Arial", Font.PLAIN, 14));
        
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }
    
    private JPanel createReceiptRowCustom(String label, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        
        p.add(l, BorderLayout.WEST);
        p.add(valueLabel, BorderLayout.EAST);
        return p;
    }

    private void handleHomeAction() {
        int response = JOptionPane.showConfirmDialog(this, 
            "Going back to Home Page will cancel your booking.\nAll data will be lost. Continue?", 
            "Cancel Booking?", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (response == JOptionPane.YES_OPTION) {
            System.out.println("Booking Cancelled. Returning to Home.");
            mainKiosk.startNewBooking(); 
        }
    }

    private void handleCompleteBooking() {
        String method = isCashSelected ? "Cash (at Counter)" : "GCash";
        
        int response = JOptionPane.showConfirmDialog(this, 
            "Confirm Booking Details:\n\n" +
            "Movie: " + mainKiosk.getCurrentBooking().getSelectedMovie().getTitle() + "\n" +
            "Amount: " + totalAmountLabel.getText() + "\n" +
            "Method: " + method + "\n\n" +
            "Proceed to finish?", 
            "Complete Booking", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            // Ito na ang tamang tawag para pumunta sa Receipt Panel:
            mainKiosk.completeBooking(); 
        }
    }

    private void addLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lbl);
    }

    private JButton createMethodButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(color);
        btn.setBackground(new Color(30, 30, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12)); 
        btn.setPreferredSize(new Dimension(110, 40)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}