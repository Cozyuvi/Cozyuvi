import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bson.Document;

public class LoginUI extends JFrame {
    MongoDBHelper db = new MongoDBHelper();

    private JTextField emailField;
    private JPasswordField passcodeField;

    public LoginUI() {
        setTitle("Admin Login");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);
        getContentPane().setBackground(new Color(250, 248, 245));
        setLayout(null);

        RoundedPanel panel = new RoundedPanel(30);
        panel.setLayout(null);
        panel.setBounds(75, 80, 350, 430);
        panel.setBackground(Color.WHITE);
        add(panel);

        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(0, 20, 350, 30);
        panel.add(title);

        JLabel subtitle = new JLabel("<html><center>Hey, Enter your details to get sign in<br>to your account</center></html>", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.DARK_GRAY);
        subtitle.setBounds(0, 50, 350, 40);
        panel.add(subtitle);

        emailField = new JTextField("Enter Email / Phone No");
        emailField.setForeground(Color.GRAY);
        emailField.setBounds(40, 100, 270, 40);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(emailField);

        emailField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (emailField.getText().equals("Enter Email / Phone No")) {
                    emailField.setText("");
                    emailField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText("Enter Email / Phone No");
                    emailField.setForeground(Color.GRAY);
                }
            }
        });

        passcodeField = new JPasswordField("Passcode");
        passcodeField.setEchoChar((char) 0);
        passcodeField.setForeground(Color.GRAY);
        passcodeField.setBounds(40, 160, 270, 40);
        passcodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passcodeField);

        passcodeField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passcodeField.getPassword()).equals("Passcode")) {
                    passcodeField.setText("");
                    passcodeField.setEchoChar('\u2022');
                    passcodeField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (String.valueOf(passcodeField.getPassword()).isEmpty()) {
                    passcodeField.setText("Passcode");
                    passcodeField.setEchoChar((char) 0);
                    passcodeField.setForeground(Color.GRAY);
                }
            }
        });

        JLabel trouble = new JLabel("Having trouble in sign in?");
        trouble.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trouble.setForeground(new Color(120, 120, 120));
        trouble.setBounds(100, 210, 200, 20);
        panel.add(trouble);

        JButton loginBtn = new JButton("Sign in");
        loginBtn.setBounds(40, 240, 270, 40);
        loginBtn.setBackground(new Color(255, 195, 123));
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> handleLogin());

    }

    private JButton createSocialButton(String text, int x) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setFocusPainted(false);
        btn.setBounds(x, 320, 80, 35);
        return btn;
    }

    private void handleLogin() {
        String username = emailField.getText().trim();
        String password = new String(passcodeField.getPassword());

        if (username.isEmpty() || username.equals("Enter Email / Phone No") || password.isEmpty() || password.equals("Passcode")) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        String hashedPassword = hashPassword(password);

        Document admin = db.getDocumentByFields("Admin", "name", username, "password", hashedPassword);

        if (admin != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            dispose();
            new DashboardUI(); // Replace with actual Dashboard class
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }

    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            super();
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
