package SadSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private BackendService backend;
    private JButton signIn;

    public Login() {
        super("Inventory System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 540);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("logo/Inventory.png").getImage());
        
        backend = BackendService.getInstance();

        GradientPanel bg = new GradientPanel(new Color(102, 78, 255), new Color(72, 149, 239));
        bg.setLayout(new GridBagLayout());
        setContentPane(bg);

        RoundedPanel card = new RoundedPanel(32);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(32, 48, 32, 48));
        card.setPreferredSize(new Dimension(360, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        bg.add(card, gbc);

        ImageIcon ic = new ImageIcon("logo/Inventory.png");
        JLabel logo = new JLabel(new ImageIcon(ic.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH)));
        logo.setAlignmentX(CENTER_ALIGNMENT);
        card.add(logo);
        card.add(Box.createVerticalStrut(12));

        JLabel title = makeLabel("Inventory System", 26, true, Color.BLACK);
        title.setAlignmentX(CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(4));

        JLabel sub = makeLabel("Sign in to Continue", 14, false, new Color(110, 110, 110));
        sub.setAlignmentX(CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(20));

        usernameField = new JTextField();
        card.add(field("Username", usernameField));
        card.add(Box.createVerticalStrut(10));
        
        passwordField = new JPasswordField();
        card.add(field("Password", passwordField));
        card.add(Box.createVerticalStrut(10));

        JPanel rememberRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rememberRow.setOpaque(false);
        JCheckBox remember = new JCheckBox("Remember me");
        remember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberRow.add(remember);
        card.add(rememberRow);
        card.add(Box.createVerticalStrut(14));

        signIn = new JButton("SIGN IN");
        stylePrimaryButton(signIn);
        signIn.addActionListener(e -> handleLogin());
        card.add(signIn);
        card.add(Box.createVerticalStrut(8));

        // Forgot password link (centered under the button)
        JButton forgotButton = linkButton("Forgot Password?", e -> {
            // Open the Forgot password window
            new Forgot().setVisible(true);
        });
        forgotButton.setAlignmentX(CENTER_ALIGNMENT);
        card.add(forgotButton);
        card.add(Box.createVerticalStrut(8));

        // Bottom line: "Don't have an account? Sign up"
        JPanel bottomLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        bottomLine.setOpaque(false);
        JLabel pre = new JLabel("Don't have an account? ");
        pre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton signUpLink = linkButton("Sign up", e -> {
            setVisible(false);
            signup s = new signup();
            s.setVisible(true);
        });
        bottomLine.add(pre);
        bottomLine.add(signUpLink);
        bottomLine.setAlignmentX(CENTER_ALIGNMENT);
        card.add(bottomLine);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        signIn.setEnabled(false);
        
        new Thread(() -> {
            boolean success = backend.login(username, password);
            
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    setVisible(false);
                    MainFrame main = new MainFrame();
                    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    main.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Login.this, 
                        "Invalid username or password.", 
                        "Login Failed", 
                        JOptionPane.ERROR_MESSAGE);
                    signIn.setEnabled(true);
                }
            });
        }).start();
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(51, 102, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        button.setAlignmentX(CENTER_ALIGNMENT);
    }

    private JPanel field(String label, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = makeLabel(label, 13, false, Color.BLACK);
        p.add(l, BorderLayout.NORTH);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(input, BorderLayout.CENTER);
        return p;
    }

    private JLabel makeLabel(String text, int size, boolean bold, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    
    private JButton linkButton(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(new Color(70, 70, 160));
        b.addActionListener(action);
        return b;
    }
}
