import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

public class ManageRoomsUI extends JFrame {

    private JTextField roomNoField, capacityField;
    private JComboBox<String> typeBox, statusBox;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private MongoDBHelper dbHelper;

    public ManageRoomsUI() {
        setTitle("Manage Rooms - PCTE Boys Hostel");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        dbHelper = new MongoDBHelper();

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(250, 250, 255));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        JLabel roomNoLabel = new JLabel("Room No:");
        roomNoLabel.setFont(labelFont);
        roomNoLabel.setBounds(40, 30, 100, 25);
        panel.add(roomNoLabel);

        roomNoField = new JTextField();
        roomNoField.setFont(fieldFont);
        roomNoField.setBounds(130, 30, 200, 25);
        panel.add(roomNoField);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(labelFont);
        typeLabel.setBounds(400, 30, 100, 25);
        panel.add(typeLabel);

        typeBox = new JComboBox<>(new String[]{"Single", "Double"});
        typeBox.setFont(fieldFont);
        typeBox.setBounds(470, 30, 200, 25);
        panel.add(typeBox);

        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setFont(labelFont);
        capacityLabel.setBounds(40, 70, 100, 25);
        panel.add(capacityLabel);

        capacityField = new JTextField();
        capacityField.setFont(fieldFont);
        capacityField.setBounds(130, 70, 200, 25);
        panel.add(capacityField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(labelFont);
        statusLabel.setBounds(400, 70, 100, 25);
        panel.add(statusLabel);

        statusBox = new JComboBox<>(new String[]{"Available", "Occupied"});
        statusBox.setFont(fieldFont);
        statusBox.setBounds(470, 70, 200, 25);
        panel.add(statusBox);

        JButton addBtn = createButton("Add");
        JButton updateBtn = createButton("Update");
        JButton deleteBtn = createButton("Delete");
        JButton clearBtn = createButton("Clear");
        JButton backBtn = createButton("Back");

        int btnY = 120;
        addBtn.setBounds(40, btnY, 110, 30);
        updateBtn.setBounds(160, btnY, 110, 30);
        deleteBtn.setBounds(280, btnY, 110, 30);
        clearBtn.setBounds(400, btnY, 110, 30);
        backBtn.setBounds(520, btnY, 110, 30);

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(backBtn);

        tableModel = new DefaultTableModel(new String[]{"Room No", "Type", "Capacity", "Status"}, 0);
        roomTable = new JTable(tableModel);
        roomTable.setFont(fieldFont);
        roomTable.setRowHeight(22);
        roomTable.getTableHeader().setFont(labelFont);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBounds(40, 180, 700, 330);
        panel.add(scrollPane);

        roomTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = roomTable.getSelectedRow();
                roomNoField.setText(tableModel.getValueAt(row, 0).toString());
                typeBox.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                capacityField.setText(tableModel.getValueAt(row, 2).toString());
                statusBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            }
        });

        addBtn.addActionListener(e -> {
            String roomNo = roomNoField.getText().trim();
            String type = (String) typeBox.getSelectedItem();
            String capacity = capacityField.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            if (roomNo.isEmpty() || capacity.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Document room = new Document("roomNo", roomNo)
                    .append("type", type)
                    .append("capacity", capacity)
                    .append("status", status);

            try {
                dbHelper.database.getCollection("rooms").insertOne(room);
                tableModel.addRow(new Object[]{roomNo, type, capacity, status});
                clearForm();
                JOptionPane.showMessageDialog(this, "Room added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Insert failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            String roomNo = roomNoField.getText().trim();
            String type = (String) typeBox.getSelectedItem();
            String capacity = capacityField.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            if (roomNo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room No is required for update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Document updateFields = new Document("type", type)
                    .append("capacity", capacity)
                    .append("status", status);

            try {
                dbHelper.database.getCollection("rooms").updateOne(
                        eq("roomNo", roomNo),
                        new Document("$set", updateFields)
                );

                int row = roomTable.getSelectedRow();
                if (row != -1) {
                    tableModel.setValueAt(roomNo, row, 0);
                    tableModel.setValueAt(type, row, 1);
                    tableModel.setValueAt(capacity, row, 2);
                    tableModel.setValueAt(status, row, 3);
                }
                clearForm();
                JOptionPane.showMessageDialog(this, "Room updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            String roomNo = roomNoField.getText().trim();

            if (roomNo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Room No to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this room?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    dbHelper.database.getCollection("rooms").deleteOne(eq("roomNo", roomNo));
                    int row = roomTable.getSelectedRow();
                    if (row != -1) {
                        tableModel.removeRow(row);
                    }
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Room deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearBtn.addActionListener(e -> clearForm());

        backBtn.addActionListener(e -> {
            dispose();
            new DashboardUI(); // make sure this class exists
        });

        add(panel);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void clearForm() {
        roomNoField.setText("");
        capacityField.setText("");
        typeBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        roomTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageRoomsUI::new);
    }
}
