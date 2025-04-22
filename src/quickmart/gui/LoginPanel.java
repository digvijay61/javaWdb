package quickmart.gui;

import javax.swing.*;
import java.awt.*;
import quickmart.management.UserManager;
import quickmart.models.*;

public class LoginPanel extends JPanel {

    private final QuickMartGUI mainFrame;
    private final UserManager userManager;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(QuickMartGUI mainFrame, UserManager userManager) {
        this.mainFrame = mainFrame;
        this.userManager = userManager;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0; gbc.gridy = 1; add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; add(passwordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Action Listeners
        loginButton.addActionListener(e -> loginUser());
        backButton.addActionListener(e -> {
            clearFields();
            mainFrame.showPanel(QuickMartGUI.MAIN_PANEL);
        });
         // Allow login on Enter press in password field
        passwordField.addActionListener(e -> loginUser());
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and Password are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = userManager.getUserByEmail(email);

            if (user != null && user.validatePassword(password)) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + user.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();

                // Navigate to the correct panel based on user role
                if (user instanceof Buyer) {
                    mainFrame.getBuyerPanel().setCurrentBuyer((Buyer) user); // Pass buyer info
                    mainFrame.showPanel(QuickMartGUI.BUYER_PANEL);
                } else if (user instanceof Seller) {
                     mainFrame.getSellerPanel().setCurrentSeller((Seller) user); // Pass seller info
                    mainFrame.showPanel(QuickMartGUI.SELLER_PANEL);
                } else {
                     JOptionPane.showMessageDialog(this, "Unknown user role.", "Error", JOptionPane.ERROR_MESSAGE);
                     mainFrame.showPanel(QuickMartGUI.MAIN_PANEL); // Go back to main
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // Clear only password on failure
            }
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Login failed due to database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace(); // Log detailed error
        }
    }

     private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }
}