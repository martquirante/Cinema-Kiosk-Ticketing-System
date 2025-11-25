import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gumagamit na ito ng BackgroundImagePanel para sa background
 * at RoundedPanel para sa "box"
 */
public class CustomerInfoPanel extends BackgroundImagePanel {

    private KioskMain mainKiosk; //ito ang ating "remote control" para utusan ang main system.
    //Ito ang variable na magsisilbing "link" o connection natin pabalik sa KioskMain class.
    
    private JPanel formPanel; 
    private List<JTextField> nameFields; // Listahan para i-store lahat ng text fields
    private JLabel subTitle;

    public CustomerInfoPanel(KioskMain mainKiosk) {
        // Tinawag ang constructor ng BackgroundImagePanel para sa image sa likod
        super(new ImageIcon("images/BGCinama_desingn.png").getImage()); 
        
        this.mainKiosk = mainKiosk;
        this.nameFields = new ArrayList<>();
        
        // Gagamitin pa rin ang GridBagLayout para i-center 'yung "box" sa gitna ng screen
        setLayout(new GridBagLayout()); 
        
        // --- CONTENT PANEL (YUNG DARK BOX) ---
        // Gumamit tayo ng (30, 30, 30, 200) - isang dark, semi-transparent black na kulay (alpha 200)
        RoundedPanel contentPanel = new RoundedPanel(30, new Color(30, 30, 30, 200));
        // BoxLayout.Y_AXIS: Ibig sabihin, ang arrangement ng laman ay pa-vertical (pababa)
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Nagdagdag ng padding SA LOOB ng "box" para hindi dikit sa gilid ang text
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30)); 
        contentPanel.setMaximumSize(new Dimension(400, 600)); // Limitahan ang lapad ng box

        // --- Header (mula sa Figma) ---
        JLabel title = new JLabel("Customer Information");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // Gitna ang alignment
        
        // set subtitle
        subTitle = new JLabel("Please provide your details");
        subTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subTitle.setForeground(Color.LIGHT_GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add natin ang title at subtitle sa loob ng box
        contentPanel.add(title);
        contentPanel.add(subTitle);
        // Spacer: Pampababa ng konti para di dikit
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // --- Dynamic Form Panel ---
        // Dito natin ilalagay ang mga Labels at TextFields (Name inputs)
        // GridBagLayout ang gamit dito para aligned ang "Label" sa kaliwa at "Input" sa kanan
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Gawing transparent para makita ang kulay ng "box" sa likod
        contentPanel.add(formPanel);
        
        /** Maglagay ng isang invisible na espasyo (space) na 20 pixels ang taas 
         * sa pagitan ng mga components.
        **/
         contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Navigation (Back/Next) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false); // ❗ Gawing transparent para makita ang "box"
        
        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        contentPanel.add(buttonPanel);
        
        // Idagdag ang contentPanel (ang "box") sa main panel (CustomerInfoPanel)
        add(contentPanel);

        // --- Action Listeners ---
        // Pag click ng Back, babalik sa Welcome Screen
        backButton.addActionListener(e -> mainKiosk.showWelcome());
        // Pag click ng Next, iche-check muna kung valid ang inputs (validateAndProceed)
        nextButton.addActionListener(e -> validateAndProceed());
    }
    
    // Method para i-setup ang fields base sa dami ng tao (totalViewers)
    public void setupPanel(int totalViewers) {
        // Linisin muna ang formPanel (remove old components) para fresh start
        formPanel.removeAll();
        nameFields.clear(); // Clear din ang listahan ng textfields
        
        // Ibahin ang subtitle text depende kung isa lang o marami ang manonood
        if (totalViewers == 1) {
            subTitle.setText("Please provide your details");
        } else {
            subTitle.setText("Please provide the names of all " + totalViewers + " viewers");
        }

        // Settings para sa positioning ng elements sa GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding sa paligid ng bawat element
        
        // Loop tayo base sa bilang ng viewers para gumawa ng Label at TextField
        for (int i = 0; i < totalViewers; i++) {
            // Logic: Kung una (0), "Your Name". Kung sunod, "Companion X Name".
            String labelText = (i == 0) ? "Your Name:" : "Companion " + i + " Name:";
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            label.setForeground(Color.WHITE);
            
            // Positioning ng Label (Left column, row 'i')
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.LINE_END; // Right align ang text ng label
            formPanel.add(label, gbc);
            
            // Paggawa ng TextField para sa input
            JTextField nameField = new JTextField(15); 
            nameField.setFont(new Font("Arial", Font.PLAIN, 16));
            
            // Positioning ng TextField (Right column, row 'i')
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.LINE_START; // Left align ang input box
            formPanel.add(nameField, gbc);
            
            // I-save ang textfield sa listahan para makuha natin ang laman nito mamaya
            nameFields.add(nameField);
        }
        
        // Sasabihan ang layout manager na mag-recalculate ng positions at sizes ng components
        formPanel.revalidate();

        // Pipilitin ang panel na mag-redraw o mag-refresh ng itsura sa screen
        formPanel.repaint();
    }
    
    // Iva-validate niya lang bago mag-proceed. Check kung may laman lahat.
    private void validateAndProceed() {
        List<String> allNames = new ArrayList<>();
        
        // Loop sa lahat ng text fields na ginawa natin kanina
        for (JTextField nameField : nameFields) {
            // .trim() para tanggalin ang extra spaces sa unahan o hulihan
            String name = nameField.getText().trim();
            
            // Kung walang laman (empty), magpapakita ng Error Message
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all name fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop na dito, huwag na tumuloy sa baba.
            }
            // Kung okay, i-add sa listahan ng mga pangalan
            allNames.add(name); 
        }
        
        // For debugging: print sa console
        System.out.println("Names collected: " + allNames);
        
        // Ipasa ang mga pangalan sa mainKiosk at lumipat sa Age Verification screen
        mainKiosk.showAgeVerification(allNames);
    }
}