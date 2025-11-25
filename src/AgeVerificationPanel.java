import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AgeVerificationPanel extends BackgroundImagePanel {

    private KioskMain mainKiosk;
    private JPanel formPanel; // Dito ilalagay ang dynamic age input fields
    private List<JTextField> ageFields; // Listahan ng JTextFields para sa mga edad
    private List<String> viewerNames; // Para sa label (e.g., "Your Age (martquirante)")
    private JLabel subTitle; // Subtitle sa taas (e.g., "Please enter the age of X viewer(s)")

    // Para sa "Note" box sa ibaba
    private RoundedPanel notePanel;
    private JLabel noteLabel;

    public AgeVerificationPanel(KioskMain mainKiosk) {
        super(new ImageIcon("images/BGCinama_desingn.png").getImage()); // Background image
        
        this.mainKiosk = mainKiosk;
        this.ageFields = new ArrayList<>();
        this.viewerNames = new ArrayList<>();
        
        setLayout(new GridBagLayout()); // Gagamitin ang GridBagLayout para i-center ang contentPanel

        // Rounded content panel na naglalaman ng lahat ng elements
        RoundedPanel contentPanel = new RoundedPanel(30, new Color(30, 30, 30, 200)); // Dark semi-transparent
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30)); // Padding sa loob
        contentPanel.setMaximumSize(new Dimension(400, 700)); // Limitahan ang laki
        contentPanel.setPreferredSize(new Dimension(380, 600)); // Preferred size para sa panel

        // --- Header ---
        JLabel title = new JLabel("Age Verification");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        subTitle = new JLabel("Please enter the age of each viewer");
        subTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subTitle.setForeground(Color.LIGHT_GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(title);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Maliit na space
        contentPanel.add(subTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // --- Dynamic Form Panel ---
        formPanel = new JPanel(new GridBagLayout()); // Gagamitin ang GridBagLayout sa loob nito
        formPanel.setOpaque(false); // Gawing transparent
        // Idagdag ang formPanel sa contentPanel
        contentPanel.add(formPanel);
        
        contentPanel.add(Box.createVerticalGlue()); // Para tulakin ang elements pataas kung masikip
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- "Note" PANEL ---
        notePanel = new RoundedPanel(15, new Color(35, 45, 90, 220)); // Blue ung rounded panel
        notePanel.setLayout(new BorderLayout());
        notePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        noteLabel = new JLabel("Note text goes here");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        noteLabel.setForeground(Color.WHITE);
        notePanel.add(noteLabel, BorderLayout.CENTER);
        
        notePanel.setVisible(false); // Nakatago by default
        contentPanel.add(notePanel);

        //Ito ay isang utility method na lumilikha ng isang 
        //Component na may fixed na laki. Hindi ito nagbabago kahit i-resize ang container.
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Navigation (Back/Next) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false); // Gawing transparent
        
        //Declare JButton
        JButton backButton = new JButton("Back");
        customizeButton(backButton); // Custom button style
        JButton nextButton = new JButton("Next");
        customizeButton(nextButton); // Custom button style
        
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);//for center alligment
        contentPanel.add(buttonPanel);
        
        add(contentPanel); // Idagdag ang contentPanel sa main panel (AgeVerificationPanel)

        // --- Action Listeners ---
        backButton.addActionListener(e -> mainKiosk.showCustomerInfoPanel());
        nextButton.addActionListener(e -> validateAndProceed());
    }
    
    // HHelper method para i-customize ang buttons
    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 63, 65)); // Default button color
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90), 1));
        button.setPreferredSize(new Dimension(100, 35)); // Consistent button size
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 83, 85)); //dark gray
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 63, 65)); //Very Dark Gra
            }
        });
    }

    /**
     * FINAL UPDATE: Stacked layout (Label above Text Field) with consistent text field width.
     * Nire-rebuild nito ang form base sa bilang ng viewers.
     */
    public void setupPanel(List<String> names) {
        this.viewerNames = names; // I-save ang mga pangalan
        int numberOfViewers = names.size();

        formPanel.removeAll();
        ageFields.clear();
        
        subTitle.setText("Please enter the age of " + numberOfViewers + " viewer(s)");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST; // Default na i-align sa kaliwa
        gbc.fill = GridBagConstraints.HORIZONTAL; // Default na i-fill ang available space
        gbc.weightx = 1.0; // Hayaan ang components na kumuha ng available horizontal space
        
        int currentGridY = 0; // Gagamitin natin 'to para i-track ang row

        for (int i = 0; i < numberOfViewers; i++) {
            
            // --- LABEL ---
            String labelText = (i == 0) ? 
                "Your Age (" + names.get(i) + ")" : 
                names.get(i) + " Age"; // Example for Companion 1 Age
            
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Arial", Font.PLAIN, 16)); // Consistent font size
            label.setForeground(Color.WHITE);
            
            gbc.gridx = 0;
            gbc.gridy = currentGridY; // Ilagay sa current row
            gbc.insets = new Insets( (i == 0 ? 0 : 15), 0, 2, 0); // Mas malaking top padding para sa bawat bagong viewer block
            formPanel.add(label, gbc);
            
            currentGridY++; // Ilipat sa susunod na row

            // --- TEXT FIELD ---
            JTextField ageField = new JTextField();
            ageField.setFont(new Font("Arial", Font.PLAIN, 16));
            ageField.setBackground(new Color(50, 50, 50)); // Darker background for text field
            ageField.setForeground(Color.WHITE);
            ageField.setCaretColor(Color.WHITE); // Cursor color
            ageField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));
            
            // Set preferred size para consistent ang lapad (parang sa Image 1)
            ageField.setPreferredSize(new Dimension(200, 35)); // Taasan ng kaunti ang taas
            ageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // Hindi lalampas sa 35 ang taas
            
            ageField.getDocument().addDocumentListener(new AgeUpdateListener());
            
            gbc.gridx = 0;
            gbc.gridy = currentGridY; // Ilagay sa row sa ilalim ng label
            gbc.insets = new Insets(0, 0, 10, 0); // Padding sa ibaba ng text field
            gbc.fill = GridBagConstraints.HORIZONTAL; // Siguraduhin na i-fill ang lapad
            gbc.weightx = 1.0; // Siguraduhin na kukunin ang available horizontal space
            formPanel.add(ageField, gbc);

            currentGridY++; // Ilipat sa susunod na row
            
            ageFields.add(ageField);
        }
        
        updateRestrictionNote(); // Tawagin ito sa simula para sa initial state ng note panel
        formPanel.revalidate();
        formPanel.repaint();
    }
    
    /**
     * Ito ang tinatawag ng listener para i-update ang "Note" box.
     */
    private void updateRestrictionNote() {
        int minAge = 121; // Simula sa mataas na edad para mahanap ang pinakabata
        boolean foundValidAge = false;

        for (JTextField field : ageFields) {
            String text = field.getText().trim();
            if (!text.isEmpty()) {
                try {
                    int age = Integer.parseInt(text);
                    if (age > 1 && age <= 120) { // Valid age range
                        foundValidAge = true;
                        if (age < minAge) {
                            minAge = age; // Hanapin ang pinakabata
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore, baka "abc" pa lang ang tina-type, hindi pa valid number
                }
            }
        }

        // Kung may valid na edad at mas bata sa 18 (highest movie restriction)
        if (foundValidAge && minAge < 18) {
            noteLabel.setText("<html><b>Note:</b> Based on the youngest age (" + minAge + " years old), " +
                              "some movies may be restricted according to ratings.</html>");
            notePanel.setVisible(true); // Ipakita ang "Note" box
        } else {
            notePanel.setVisible(false); // Itago kung walang restriction o walang valid input
        }
        // Mahalaga: revalidate at repaint para mag-update ang layout kapag nagbago ang visibility ng notePanel
        revalidate();
        repaint();
    }

    /**
     * Inner Class: Ang DocumentListener para sa JTextFields.
     * Tinatawag nito ang updateRestrictionNote() sa bawat pagbabago ng text.
     */
    private class AgeUpdateListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateRestrictionNote(); // Tawagin 'pag may tinype
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            updateRestrictionNote(); // Tawagin 'pag may binura
        }
        @Override
        public void changedUpdate(DocumentEvent e) {
            // Hindi ito masyadong ginagamit sa plain text fields
        }
    }
    
    // Ang 'validateAndProceed' method para sa pag-check ng inputs at pag-proceed sa next screen
    private void validateAndProceed() {
        List<Integer> ages = new ArrayList<>();
        
        for (int i = 0; i < ageFields.size(); i++) {
            String input = ageFields.get(i).getText().trim();
            
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all age fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Huminto at maghintay ng valid input
            }
            
            try {
                int age = Integer.parseInt(input);
                if (age <= 0 || age > 120) { // Valid age range
                    JOptionPane.showMessageDialog(this, "Please enter a valid age (1-120) for " + viewerNames.get(i) + ".", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Huminto at maghintay ng valid input
                }
                ages.add(age); // Idagdag ang valid na edad
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for " + viewerNames.get(i) + ". Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Huminto at maghintay ng valid input
            }
        }
        
        System.out.println("Ages collected: " + ages);
        mainKiosk.showMovieSelection(ages); // Mag-proceed sa movie selection screen
    }
}