import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomePanel extends JPanel {

    private KioskMain mainKiosk;

    private JButton nextButton;
    private JPanel soloPanel;
    private JPanel companionPanel;

    private Border defaultBorder;
    private Border selectedBorder;

    private int selectedViewerType = 0;

    public WelcomePanel(KioskMain mainKiosk) {
        this.mainKiosk = mainKiosk;
        
        // Ito ung ung parang button or line border if naka deafaualt is walang kulay ung border niya
        defaultBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 55, 90), 2), 
                new EmptyBorder(40, 20, 40, 20) 
        );
        // na select na is may kulay pink na is may kulay pink na line na lalabas
        selectedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 130, 255), 3), 
                new EmptyBorder(40, 20, 40, 20) 
        );
        // Ung layout ng Line Border
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 25, 60)); 
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        // if true to is dib makikita ung text mo dahil may kulay outing box siya
        headerPanel.setOpaque(false); 

        JLabel title = new JLabel("Welcome to CinemaBook");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Let's start your booking");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(subtitle);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.1; 
        gbc.insets = new Insets(20, 20, 10, 20);
        add(headerPanel, gbc);

        // --- Middle Question ---
        JLabel questionLabel = new JLabel("Are you watching alone or with companions?");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        questionLabel.setForeground(Color.WHITE);
        
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.insets = new Insets(10, 20, 20, 20);
        add(questionLabel, gbc);

        // --- Options Panel ---
        JPanel optionsContainer = new JPanel(new GridBagLayout());
        optionsContainer.setOpaque(false);
        GridBagConstraints gbcOptions = new GridBagConstraints();

        soloPanel = createOptionPanel(
                "Watching Solo", 
                "Just me", 
                "images/icon_solo.png"
        );
        companionPanel = createOptionPanel(
                "With Companions", 
                "2 or more people", 
                "images/icon_group.png" // 
        );
        // eseset ung Option Pane base sa layout na gusto ko
        gbcOptions.gridx = 0;
        gbcOptions.gridy = 0;
        gbcOptions.fill = GridBagConstraints.HORIZONTAL;
        gbcOptions.insets = new Insets(0, 0, 20, 0); 
        optionsContainer.add(soloPanel, gbcOptions);

        gbcOptions.gridy = 1;
        optionsContainer.add(companionPanel, gbcOptions);
        
        gbc.gridy = 2;
        gbc.weighty = 0.6; 
        gbc.insets = new Insets(0, 40, 0, 40); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(optionsContainer, gbc);

        //"Next" Button Panel
        JPanel nextButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextButtonPanel.setOpaque(false);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setEnabled(false); 
        nextButtonPanel.add(nextButton);
        
        gbc.gridy = 3;
        gbc.weighty = 0.1; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_END; 
        gbc.insets = new Insets(20, 20, 20, 40);
        add(nextButtonPanel, gbc);

        // --- ACTION LISTENERS ---
        // If na click ung dalwang option Pane
        soloPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedViewerType = 1; 
                soloPanel.setBorder(selectedBorder);
                companionPanel.setBorder(defaultBorder);
                nextButton.setEnabled(true);
            }
        });

        companionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedViewerType = 2; 
                soloPanel.setBorder(defaultBorder);
                companionPanel.setBorder(selectedBorder);
                nextButton.setEnabled(true);
            }
        });

        nextButton.addActionListener(e -> {
            if (selectedViewerType == 1) {
                mainKiosk.showCustomerInfo(1);
            } else if (selectedViewerType == 2) {
                askForCompanions();
            }
        });
    }
    
    //Private method for Option Panin
    private JPanel createOptionPanel(String title, String subtitle, String iconPath) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 35, 70)); 
        panel.setBorder(defaultBorder);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        //Try Catch for image
        try {
            ImageIcon originalIcon = new ImageIcon(iconPath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            panel.add(iconLabel); 
            panel.add(Box.createRigidArea(new Dimension(0, 15))); 

        } catch (Exception e) {
            System.err.println("Warning: Icon not found at " + iconPath);
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5))); 
        panel.add(subtitleLabel);
        
        return panel;
    }
    //If na click at na next na ung Companion is ilan kasam niya
    // Option Pne pa rin
    private void askForCompanions() {
        String input = JOptionPane.showInputDialog(this,
                "Enter total number of viewers (including you):",
                "Number of Viewers",
                JOptionPane.PLAIN_MESSAGE);
        //Try catch sa inout ng user
        try {
            if (input != null && !input.trim().isEmpty()) {
                int count = Integer.parseInt(input);
                //if zero ung input ng user is may lilitaw na message
                if (count > 1) {
                    mainKiosk.showCustomerInfo(count);
                } else {
                    // na na di pwede ung mas mababa sa 1
                    JOptionPane.showMessageDialog(this,
                            "Please enter a number greater than 1.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            // if ung error message na un na na click na okay is babalik lang sa Option Pane
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input. Please enter a number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //reset Panell
    public void resetPanel() {
        selectedViewerType = 0;
        //seter ng dalwang Option Pane
        soloPanel.setBorder(defaultBorder);
        companionPanel.setBorder(defaultBorder);
        // If wala pa naseselect is di maclilick ung next
        nextButton.setEnabled(false);
    }
}