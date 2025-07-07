import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardUI extends JFrame {

    public DashboardUI() {
        setTitle("PCTE Boys Hostel Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setPreferredSize(new Dimension(1000, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel title = new JLabel("PCTE Boys Hostel Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(40, 40, 40));
        headerPanel.add(title, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(new Color(255, 85, 115));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel welcomeLabel = new JLabel("Welcome to Hostel Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(50, 50, 50));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        cardPanel.setOpaque(false);

        cardPanel.add(createStatCard("👨‍🏫", "Staff", "10", new Color(255, 235, 238)));
        cardPanel.add(createStatCard("👨‍🎓", "Students", "120", new Color(232, 245, 233)));
        cardPanel.add(createStatCard("🛏", "Rooms", "60", new Color(227, 242, 253)));

        mainPanel.add(cardPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        JPanel gridButtonPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridButtonPanel.setOpaque(false);

        gridButtonPanel.add(createMainButton("Manage Students"));
        gridButtonPanel.add(createMainButton("Manage Rooms"));
        gridButtonPanel.add(createMainButton("Complaints"));
        gridButtonPanel.add(createMainButton("Mess & Menu"));
        gridButtonPanel.add(createMainButton("Hostel Details"));

        mainPanel.add(gridButtonPanel);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(255, 255, 255));
        footerPanel.setPreferredSize(new Dimension(1000, 40));
        JLabel footer = new JLabel("2025 © PCTE Boys Hostel | Developed by Anmol, Rajan, Parshant, Akshat");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(100, 100, 100));
        footerPanel.add(footer);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createStatCard(String emoji, String title, String value, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1)
        ));
        card.setPreferredSize(new Dimension(200, 100));
        card.setMaximumSize(new Dimension(200, 100));
        card.setAlignmentY(Component.CENTER_ALIGNMENT);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(new Color(60, 60, 60));

        JLabel count = new JLabel(value);
        count.setFont(new Font("Segoe UI", Font.BOLD, 22));
        count.setAlignmentX(Component.CENTER_ALIGNMENT);
        count.setForeground(new Color(33, 150, 243));

        card.setBackground(bgColor);
        card.add(icon);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(label);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(count);

        return card;
    }

    private JButton createMainButton(String label) {
        JButton btn = new JButton(label);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(255, 255, 255));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(60, 60, 60));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(240, 240, 240));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });

        if (label.equals("Manage Students")) {
            btn.addActionListener(e -> new ManageStudentsUI());
        } else if (label.equals("Manage Rooms")) {
            btn.addActionListener(e -> new ManageRoomsUI());
        } else if (label.equals("Complaints")) {
            btn.addActionListener(e -> new ManageComplaintsUI());
        } else if (label.equals("Mess & Menu")) {
            btn.addActionListener(e -> new ManageMessUI());
        } else if (label.equals("Hostel Details")) {
            btn.addActionListener(e -> new HostelDetailsUI());
        }

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardUI());
    }
}
