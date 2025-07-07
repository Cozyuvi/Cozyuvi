import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;

public class ManageMessUI extends JFrame {

    private JComboBox<String> dayBox;
    private JTextArea breakfastArea, lunchArea, dinnerArea;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private MongoDBHelper dbHelper;

    public ManageMessUI() {
        setTitle("Manage Mess Menu - PCTE Boys Hostel");
        setSize(750, 570);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        dbHelper = new MongoDBHelper();
        dbHelper.ensureMessTTLIndex(); // TTL setup for mess collection

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 255, 240));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel dayLabel = new JLabel("Day:");
        dayLabel.setFont(labelFont);
        dayLabel.setBounds(30, 30, 100, 25);
        panel.add(dayLabel);

        dayBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});
        dayBox.setBounds(100, 30, 200, 25);
        panel.add(dayBox);

        JLabel breakfastLabel = new JLabel("Breakfast:");
        breakfastLabel.setFont(labelFont);
        breakfastLabel.setBounds(30, 70, 100, 25);
        panel.add(breakfastLabel);

        breakfastArea = new JTextArea();
        JScrollPane bScroll = new JScrollPane(breakfastArea);
        bScroll.setBounds(130, 70, 530, 40);
        panel.add(bScroll);

        JLabel lunchLabel = new JLabel("Lunch:");
        lunchLabel.setFont(labelFont);
        lunchLabel.setBounds(30, 120, 100, 25);
        panel.add(lunchLabel);

        lunchArea = new JTextArea();
        JScrollPane lScroll = new JScrollPane(lunchArea);
        lScroll.setBounds(130, 120, 530, 40);
        panel.add(lScroll);

        JLabel dinnerLabel = new JLabel("Dinner:");
        dinnerLabel.setFont(labelFont);
        dinnerLabel.setBounds(30, 170, 100, 25);
        panel.add(dinnerLabel);

        dinnerArea = new JTextArea();
        JScrollPane dScroll = new JScrollPane(dinnerArea);
        dScroll.setBounds(130, 170, 530, 40);
        panel.add(dScroll);

        JButton saveBtn = new JButton("Save to Mess");
        JButton clearBtn = new JButton("Clear");
        JButton backBtn = new JButton("Back");

        saveBtn.setBounds(130, 230, 130, 30);
        clearBtn.setBounds(270, 230, 110, 30);
        backBtn.setBounds(390, 230, 110, 30);

        panel.add(saveBtn);
        panel.add(clearBtn);
        panel.add(backBtn);

        tableModel = new DefaultTableModel(new String[]{"Day", "Breakfast", "Lunch", "Dinner"}, 0);
        menuTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBounds(30, 280, 670, 230);
        panel.add(scrollPane);

        saveBtn.addActionListener(e -> {
            String day = dayBox.getSelectedItem().toString();
            String breakfast = breakfastArea.getText().trim();
            String lunch = lunchArea.getText().trim();
            String dinner = dinnerArea.getText().trim();

            if (breakfast.isEmpty() || lunch.isEmpty() || dinner.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Document messEntry = new Document("day", day)
                    .append("breakfast", breakfast)
                    .append("lunch", lunch)
                    .append("dinner", dinner)
                    .append("createdAt", new java.util.Date()); // TTL field

            try {
                dbHelper.database.getCollection("mess").insertOne(messEntry);
                JOptionPane.showMessageDialog(this, "Menu saved. Entry will auto-delete in 24 hours.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Update table (UI)
            boolean updated = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(day)) {
                    tableModel.setValueAt(breakfast, i, 1);
                    tableModel.setValueAt(lunch, i, 2);
                    tableModel.setValueAt(dinner, i, 3);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                tableModel.addRow(new Object[]{day, breakfast, lunch, dinner});
            }

            clearFields();
        });

        clearBtn.addActionListener(e -> clearFields());

        backBtn.addActionListener(e -> {
            dispose();
            new DashboardUI(); // Replace with your actual dashboard class
        });

        menuTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = menuTable.getSelectedRow();
                if (row != -1) {
                    dayBox.setSelectedItem(tableModel.getValueAt(row, 0).toString());
                    breakfastArea.setText(tableModel.getValueAt(row, 1).toString());
                    lunchArea.setText(tableModel.getValueAt(row, 2).toString());
                    dinnerArea.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        add(panel);
        setVisible(true);
    }

    private void clearFields() {
        breakfastArea.setText("");
        lunchArea.setText("");
        dinnerArea.setText("");
        menuTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageMessUI::new);
    }
}
