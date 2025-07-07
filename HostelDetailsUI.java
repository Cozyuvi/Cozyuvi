import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HostelDetailsUI extends JFrame {
    public HostelDetailsUI() {
        setTitle("Hostel Details - PCTE Boys Hostel");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel (Navigation)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 49, 63));
        headerPanel.setPreferredSize(new Dimension(600, 70));

        // Title on left
        JLabel title = new JLabel("   🏠 PCTE Boys Hostel Information");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);

        // Back button on right
        JButton backBtn = new JButton("⬅ Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBackground(new Color(41, 128, 185));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backBtn.addActionListener(e -> {
            dispose();
            new DashboardUI(); // Return to Dashboard
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(backBtn);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        String[] details = {
                "📍 Location: Ludhiana, Punjab",
                "🏢 Capacity: 150 Students",
                "🛏️ Rooms: 60 Double Sharing",
                "🧑‍🍳 Mess: Nutritious Veg & Non-Veg Meals",
                "📶 Wi-Fi: 24x7 High-Speed Internet",
                "🧺 Laundry: Weekly Service Available",
                "🏋️ Gym: Equipped with modern machines",
                "🛡️ Security: 24x7 Guards + CCTV Surveillance"
        };

        for (String text : details) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            contentPanel.add(label);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HostelDetailsUI::new);
    }
}
