package quickmart.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {

    private final QuickMartGUI mainFrame;

    public MainPanel(QuickMartGUI mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component on a new row
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Welcome to QuickMart!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton adminButton = new JButton("Admin");
        JButton exitButton = new JButton("Exit");

        // --- Style Buttons ---
        Dimension buttonSize = new Dimension(150, 40);
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        registerButton.setPreferredSize(buttonSize);
        registerButton.setFont(buttonFont);
        loginButton.setPreferredSize(buttonSize);
        loginButton.setFont(buttonFont);
        adminButton.setPreferredSize(buttonSize);
        adminButton.setFont(buttonFont);
        exitButton.setPreferredSize(buttonSize);
        exitButton.setFont(buttonFont);
        // --- End Style ---


        gbc.weighty = 0.5; // Push title up slightly
        add(titleLabel, gbc);
        gbc.weighty = 0; // Reset weighty


        gbc.fill = GridBagConstraints.NONE; // Don't stretch buttons horizontally
        gbc.anchor = GridBagConstraints.CENTER; // Center buttons

        add(registerButton, gbc);
        add(loginButton, gbc);
        add(adminButton, gbc);

        gbc.weighty = 0.5; // Push exit button down
        gbc.anchor = GridBagConstraints.PAGE_END; // Anchor exit button to bottom
        add(exitButton, gbc);


        // --- Action Listeners ---
        registerButton.addActionListener(e -> mainFrame.showPanel(QuickMartGUI.REGISTER_PANEL));
        loginButton.addActionListener(e -> mainFrame.showPanel(QuickMartGUI.LOGIN_PANEL));
        adminButton.addActionListener(e -> mainFrame.showPanel(QuickMartGUI.ADMIN_PANEL)); // Assuming admin doesn't need login for now
        exitButton.addActionListener(e -> mainFrame.exitApplication());

    }
}
