package SadSystem;

import javax.swing.*;
import java.awt.*;

public class Forgot extends JFrame {
    public Forgot() {
        setTitle("Password Recovery");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Password Recovery", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        JLabel emailLabel = new JLabel("Enter your email:");
        JTextField emailField = new JTextField();
        
        JButton recoverButton = new JButton("Recover Password");
        recoverButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "If an account exists with this email, a password reset link has been sent.");
            dispose();
        });
        
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(recoverButton);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        add(panel);
    }
}
