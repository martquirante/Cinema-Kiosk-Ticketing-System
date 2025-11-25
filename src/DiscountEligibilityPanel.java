import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.io.File;

public class DiscountEligibilityPanel extends RoundedPanel {

    private KioskMain mainKiosk;
    private DiscountOptionPanel yesPanel;
    private DiscountOptionPanel noPanel;
    private JPanel discountInputPanel;
    private JTextField numStudentSeniorTicketsField;
    private JLabel maxTicketsLabel;
    private JButton nextButton;
    private JButton backButton;

    private boolean isDiscountSelected = false;
    private int totalTickets = 0;

    // Colors based on screenshot
    private final Color COLOR_BG_DARK = new Color(20, 20, 30, 200);
    private final Color COLOR_PURPLE_FILL = new Color(75, 0, 130); // Deep Purple for selected "No"
    private final Color COLOR_CARD_BG = new Color(30, 30, 40); // Default Dark Card
    private final Color COLOR_ACCENT_GREEN = new Color(0, 200, 83);
    private final Color COLOR_ACCENT_PURPLE = new Color(180, 160, 255);

    public DiscountEligibilityPanel(KioskMain mainKiosk) {
        super(30, new Color(20, 20, 30, 230)); // Dark semi-transparent container
        this.mainKiosk = mainKiosk;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 40, 30, 40)); // Padding around everything

        // --- HEADER ---
        addLabel("Discount Eligibility", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        add(Box.createRigidArea(new Dimension(0, 5)));
        addLabel("Check if you qualify for discounts", new Font("Arial", Font.PLAIN, 14), Color.GRAY);

        add(Box.createRigidArea(new Dimension(0, 25)));

        // --- QUESTION ---
        addLabel("Are you a student or senior", new Font("Arial", Font.BOLD, 18), Color.WHITE);
        addLabel("citizen?", new Font("Arial", Font.BOLD, 18), Color.WHITE);
        add(Box.createRigidArea(new Dimension(0, 5)));
        addLabel("20% discount available", new Font("Arial", Font.PLAIN, 12), Color.GRAY);

        add(Box.createRigidArea(new Dimension(0, 20)));

        // --- VERTICAL CARDS ---
        // Use a container to hold cards but keep alignment centered
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);
        cardsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // YES CARD
        yesPanel = new DiscountOptionPanel(
            "images/YesDiscount.png",
            "Yes", "Student / Senior Citizen", "Get 20% off!",
            COLOR_ACCENT_GREEN
        );

        // NO CARD
        noPanel = new DiscountOptionPanel(
            "images/NoDiscount.png",
            "No", "Regular customer", "Regular pricing",
            COLOR_ACCENT_PURPLE
        );

        // Listeners
        yesPanel.addMouseListener(new OptionClickListener(true));
        noPanel.addMouseListener(new OptionClickListener(false));

        // Add to container with gap
        cardsContainer.add(yesPanel);
        cardsContainer.add(Box.createRigidArea(new Dimension(0, 15))); // Gap between cards
        cardsContainer.add(noPanel);

        add(cardsContainer);
        add(Box.createRigidArea(new Dimension(0, 15)));

        // --- INPUT FIELD (Starts Hidden) ---
        discountInputPanel = new JPanel();
        discountInputPanel.setLayout(new BoxLayout(discountInputPanel, BoxLayout.Y_AXIS));
        discountInputPanel.setOpaque(false);
        discountInputPanel.setVisible(false); // Hidden by default
        discountInputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel howManyLabel = new JLabel("How many tickets?");
        howManyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        howManyLabel.setForeground(Color.WHITE);
        howManyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        discountInputPanel.add(howManyLabel);

        discountInputPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        numStudentSeniorTicketsField = new JTextField("0");
        numStudentSeniorTicketsField.setMaximumSize(new Dimension(100, 40));
        numStudentSeniorTicketsField.setHorizontalAlignment(JTextField.CENTER);
        numStudentSeniorTicketsField.setFont(new Font("Arial", Font.BOLD, 20));
        numStudentSeniorTicketsField.setBackground(new Color(50, 50, 50));
        numStudentSeniorTicketsField.setForeground(Color.WHITE);
        numStudentSeniorTicketsField.setCaretColor(Color.WHITE);
        numStudentSeniorTicketsField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Key Listener para bawal mag type ng letters
        numStudentSeniorTicketsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    evt.consume();
                }
            }
        });

        // ❗ ITO ANG FIX: DocumentListener para Real-time validation habang nagta-type
        numStudentSeniorTicketsField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateTicketInput(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateTicketInput(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateTicketInput(); }
        });

        discountInputPanel.add(numStudentSeniorTicketsField);

        maxTicketsLabel = new JLabel("Max: 0");
        maxTicketsLabel.setForeground(Color.GRAY);
        maxTicketsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        discountInputPanel.add(maxTicketsLabel);

        add(discountInputPanel);

        // Push buttons to bottom
        add(Box.createVerticalGlue());

        // --- BUTTONS (Back & Next) ---
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(500, 50));

        backButton = new JButton(" < Back ");
        styleButton(backButton, Color.WHITE, Color.BLACK); // White btn

        nextButton = new JButton(" Next ");
        styleButton(nextButton, new Color(10, 10, 10), Color.WHITE); // Black btn
        nextButton.setEnabled(false);

        btnPanel.add(backButton, BorderLayout.WEST);
        btnPanel.add(nextButton, BorderLayout.EAST);

        add(btnPanel);

        // Action Listeners
        backButton.addActionListener(e -> mainKiosk.showShowtimeSelection(null));
        nextButton.addActionListener(e -> {
            int tickets = 0;
            if (isDiscountSelected) {
                try {
                    tickets = NumberFormat.getIntegerInstance().parse(numStudentSeniorTicketsField.getText()).intValue();
                } catch (ParseException ex) { tickets = 0; }
            }
            mainKiosk.proceedToPayment(tickets);
        });
    }

    // --- UI HELPERS ---
    private void addLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lbl);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private ImageIcon loadIcon(String path) {
        if (new java.io.File(path).exists()) {
            return new ImageIcon(path);
        } else {
            System.err.println("Icon missing: " + path);
            return null;
        }
    }

    public void setTotalTickets(int tickets) {
        this.totalTickets = tickets;
        if (tickets == 1) {
             maxTicketsLabel.setText("Max: " + tickets + " ticket");
        } else {
             maxTicketsLabel.setText("Max: " + tickets + " tickets");
        }
        numStudentSeniorTicketsField.setText("0");
        
        // Reset states
        yesPanel.setCardState(false, false);
        noPanel.setCardState(false, false);
        discountInputPanel.setVisible(false);
        nextButton.setEnabled(false);
        isDiscountSelected = false;
        
        validateTicketInput();
    }

    private void validateTicketInput() {
        // Ito ang nagchecheck if valid ang number
        String text = numStudentSeniorTicketsField.getText().trim();
        
        // Check if empty
        if (text.isEmpty()) {
            nextButton.setEnabled(false);
            return;
        }

        try {
            int entered = Integer.parseInt(text);
            
            // Auto-correct if lagpas sa max
            if (entered > totalTickets) {
                // Avoid recursive loop by invoking later if modifying text inside listener
                SwingUtilities.invokeLater(() -> {
                    numStudentSeniorTicketsField.setText(String.valueOf(totalTickets));
                });
                entered = totalTickets;
            } 
            
            updateNextButtonState(entered);
            
        } catch (NumberFormatException ex) { 
            // Invalid number
            nextButton.setEnabled(false);
        }
    }

    private void updateNextButtonState(int currentInput) {
        if (isDiscountSelected) {
             // If "Yes", must have valid input
             if (!discountInputPanel.isVisible()) {
                 // Solo viewer (hidden input), always valid
                 nextButton.setEnabled(true);
             } else {
                 // Group viewer, must be > 0 and <= total
                 nextButton.setEnabled(currentInput > 0 && currentInput <= totalTickets);
             }
        } else {
            // If "No", always enabled
            nextButton.setEnabled(true);
        }
    }
    
    // Overload for generic updates
    private void updateNextButtonState() {
        try {
            int t = Integer.parseInt(numStudentSeniorTicketsField.getText());
            updateNextButtonState(t);
        } catch (Exception e) {
            updateNextButtonState(0);
        }
    }

    // --- INNER CLASS: VERTICAL CARD ---
    private class DiscountOptionPanel extends RoundedPanel {
        private JLabel iconLabel;
        private JLabel titleLabel;
        private JLabel descLabel1;
        private JLabel descLabel2;

        public DiscountOptionPanel(String iconPath, String title, String d1, String d2, Color accentColor) {
            super(20, COLOR_CARD_BG); // Default background
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(280, 160));
            setMaximumSize(new Dimension(280, 160));
            setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Icon
            ImageIcon ic = loadIcon(iconPath);
            if(ic != null) {
                Image sc = ic.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                iconLabel = new JLabel(new ImageIcon(sc));
            } else {
                iconLabel = new JLabel("(No Icon)");
                iconLabel.setForeground(Color.RED);
            }
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(iconLabel);
            add(Box.createRigidArea(new Dimension(0, 10)));

            // Title
            titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createRigidArea(new Dimension(0, 5)));

            // Desc 1
            descLabel1 = new JLabel(d1);
            descLabel1.setFont(new Font("Arial", Font.PLAIN, 12));
            descLabel1.setForeground(Color.LIGHT_GRAY);
            descLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(descLabel1);

            // Desc 2
            descLabel2 = new JLabel(d2);
            descLabel2.setFont(new Font("Arial", Font.BOLD, 13));
            descLabel2.setForeground(accentColor);
            descLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(descLabel2);
        }

        public void setCardState(boolean isSelected, boolean isPurpleMode) {
            if (isSelected) {
                if (isPurpleMode) {
                    setBackground(COLOR_PURPLE_FILL); 
                    setBorder(BorderFactory.createLineBorder(new Color(160, 100, 255), 2));
                } else {
                    setBackground(COLOR_CARD_BG); 
                    setBorder(BorderFactory.createLineBorder(COLOR_ACCENT_GREEN, 2)); 
                }
            } else {
                setBackground(COLOR_CARD_BG);
                setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
            }
            repaint();
        }
    }

    // --- CLICK LISTENER ---
    private class OptionClickListener extends MouseAdapter {
        private boolean isYes;
        public OptionClickListener(boolean isYes) { this.isYes = isYes; }

        @Override
        public void mouseClicked(MouseEvent e) {
            isDiscountSelected = isYes;

            if (isYes) {
                yesPanel.setCardState(true, false);
                noPanel.setCardState(false, false);

                if (totalTickets == 1) {
                    discountInputPanel.setVisible(false);
                    numStudentSeniorTicketsField.setText("1");
                } else {
                    discountInputPanel.setVisible(true);
                    numStudentSeniorTicketsField.setText("0");
                }
                validateTicketInput();
            } else {
                yesPanel.setCardState(false, false);
                noPanel.setCardState(true, true);
                discountInputPanel.setVisible(false);
                nextButton.setEnabled(true);
            }
            revalidate();
            repaint();
        }
    }
}