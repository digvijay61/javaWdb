package quickmart.gui;

import javax.swing.*;
import java.awt.*;
import quickmart.management.UserManager;
import quickmart.models.*;

public class RegisterPanel extends JPanel {

    private final QuickMartGUI mainFrame;
    private final UserManager userManager;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton buyerRadio;
    private JRadioButton sellerRadio;
    private ButtonGroup roleGroup;

    public RegisterPanel(QuickMartGUI mainFrame, UserManager userManager) {
        this.mainFrame = mainFrame;
        this.userManager = userManager;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JLabel roleLabel = new JLabel("Role:");
        buyerRadio = new JRadioButton("Buyer", true); // Default selected
        sellerRadio = new JRadioButton("Seller");
        roleGroup = new ButtonGroup();
        roleGroup.add(buyerRadio);
        roleGroup.add(sellerRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.add(buyerRadio);
        radioPanel.add(sellerRadio);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0; gbc.gridy = 1; add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; add(roleLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; add(radioPanel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);


        // Action Listeners
        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            clearFields();
            mainFrame.showPanel(QuickMartGUI.MAIN_PANEL);
        });
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        boolean isBuyer = buyerRadio.isSelected();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
             JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if email already exists (optional but good)
        if (userManager.getUserByEmail(email) != null) {
            JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user;
        if (isBuyer) {
            user = new Buyer(name, email, password) { // Anonymous inner class for displayInfo
                @Override public void displayInfo() {} // Not needed for GUI context really
            };
        } else {
            user = new Seller(name, email, password) { // Anonymous inner class
                @Override public void displayInfo() {} // Not needed for GUI context really
            };
        }

        try {
             userManager.registerUser(user);
             JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
             clearFields();
             mainFrame.showPanel(QuickMartGUI.LOGIN_PANEL); // Go to login after registration
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace(); // Log detailed error
        }
    }

    void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        buyerRadio.setSelected(true);
    }
}

