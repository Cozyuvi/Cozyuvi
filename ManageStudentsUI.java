import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.eq;

public class ManageStudentsUI extends JFrame {

    private JTextField nameField, contactField, dateField, rollNumberField;
    private JComboBox<String> genderBox, roomDropdown;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public ManageStudentsUI() {
        setTitle("Manage Students - PCTE Boys Hostel");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(250, 250, 250));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setBounds(30, 30, 100, 25);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(120, 30, 200, 25);
        panel.add(nameField);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(labelFont);
        contactLabel.setBounds(360, 30, 100, 25);
        panel.add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(450, 30, 200, 25);
        panel.add(contactField);

        JLabel roomLabel = new JLabel("Room No:");
        roomLabel.setFont(labelFont);
        roomLabel.setBounds(30, 70, 100, 25);
        panel.add(roomLabel);

        roomDropdown = new JComboBox<>();
        roomDropdown.setBounds(120, 70, 200, 25);
        panel.add(roomDropdown);
        loadAvailableRooms();

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(labelFont);
        genderLabel.setBounds(360, 70, 100, 25);
        panel.add(genderLabel);

        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setBounds(450, 70, 200, 25);
        panel.add(genderBox);

        JLabel dateLabel = new JLabel("Admission Date:");
        dateLabel.setFont(labelFont);
        dateLabel.setBounds(30, 110, 120, 25);
        panel.add(dateLabel);

        dateField = new JTextField("YYYY-MM-DD");
        dateField.setBounds(150, 110, 180, 25);
        panel.add(dateField);

        JLabel rollLabel = new JLabel("Roll No:");
        rollLabel.setFont(labelFont);
        rollLabel.setBounds(360, 110, 100, 25);
        panel.add(rollLabel);

        rollNumberField = new JTextField();
        rollNumberField.setBounds(450, 110, 200, 25);
        panel.add(rollNumberField);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton backBtn = new JButton("Back");

        int y = 160;
        addBtn.setBounds(30, y, 110, 30);
        updateBtn.setBounds(150, y, 110, 30);
        deleteBtn.setBounds(270, y, 110, 30);
        clearBtn.setBounds(390, y, 110, 30);
        backBtn.setBounds(510, y, 110, 30);

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(backBtn);

        tableModel = new DefaultTableModel(new String[]{"ID", "Roll No", "Name", "Room", "Contact", "Gender", "Admission Date"}, 0);
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBounds(30, 210, 680, 300);
        panel.add(scrollPane);

        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = studentTable.getSelectedRow();
                rollNumberField.setText(tableModel.getValueAt(row, 1).toString());
                nameField.setText(tableModel.getValueAt(row, 2).toString());
                roomDropdown.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                contactField.setText(tableModel.getValueAt(row, 4).toString());
                genderBox.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                dateField.setText(tableModel.getValueAt(row, 6).toString());
            }
        });

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String rollnumber = rollNumberField.getText().trim();
            String room = (String) roomDropdown.getSelectedItem();
            String gender = genderBox.getSelectedItem().toString();
            String date = dateField.getText().trim();

            if (name.isEmpty() || contact.isEmpty() || room == null || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String rollNumber = rollnumber;
                String plainPassword = generateRandomPassword(8);
                String hashedPassword = hashPassword(plainPassword);

                MongoDBHelper db = new MongoDBHelper();

                // Insert student
                Document student = new Document("rollNumber", rollNumber)
                        .append("name", name)
                        .append("room", room)
                        .append("contact", contact)
                        .append("gender", gender)
                        .append("admissionDate", date)
                        .append("password", hashedPassword);
                db.getCollection("Student").insertOne(student);

                // Update room occupation
                MongoCollection<Document> roomCollection = db.getCollection("rooms");
                roomCollection.updateOne(eq("roomNumber", room), Updates.inc("occupation", 1));
                db.close();

                savePasswordToCSV(rollNumber, plainPassword);

                int id = tableModel.getRowCount() + 1;
                tableModel.addRow(new Object[]{id, rollNumber, name, room, contact, gender, date});
                clearForm();
                loadAvailableRooms(); // Refresh available rooms

                JOptionPane.showMessageDialog(this, "Student saved with roll: " + rollNumber, "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving student", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Update functionality not adapted for room change.", "Info", JOptionPane.INFORMATION_MESSAGE));

        deleteBtn.addActionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tableModel.removeRow(row);
            clearForm();
        });

        clearBtn.addActionListener(e -> clearForm());
        backBtn.addActionListener(e -> {
            dispose();
            new DashboardUI();
        });

        add(panel);
        setVisible(true);
    }

    private void loadAvailableRooms() {
        roomDropdown.removeAllItems();
        try {
            MongoDBHelper db = new MongoDBHelper();
            MongoCollection<Document> rooms = db.getCollection("rooms");

            for (Document doc : rooms.find()) {
                int capacity = 0;
                int occupation = 0;
                String status = doc.getString("status");
                String roomNo = doc.getString("roomNo");

                // Handle both Integer and String capacity fields
                try {
                    Object capacityObj = doc.get("capacity");
                    if (capacityObj instanceof Integer) {
                        capacity = (Integer) capacityObj;
                    } else if (capacityObj instanceof String) {
                        capacity = Integer.parseInt((String) capacityObj);
                    }
                } catch (Exception ignored) {}

                // Handle both Integer and String occupation fields
                try {
                    Object occupationObj = doc.get("occupation");
                    if (occupationObj instanceof Integer) {
                        occupation = (Integer) occupationObj;
                    } else if (occupationObj instanceof String) {
                        occupation = Integer.parseInt((String) occupationObj);
                    }
                } catch (Exception ignored) {}

                if ("Available".equalsIgnoreCase(status) && occupation < capacity) {
                    roomDropdown.addItem(roomNo);
                }
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load rooms", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void clearForm() {
        nameField.setText("");
        contactField.setText("");
        rollNumberField.setText("");
        genderBox.setSelectedIndex(0);
        dateField.setText("YYYY-MM-DD");
        roomDropdown.setSelectedIndex(-1);
        studentTable.clearSelection();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void savePasswordToCSV(String rollNumber, String plainPassword) throws IOException {
        String filePath = "StudentPasswords.csv";
        FileWriter writer = new FileWriter(filePath, true);
        writer.append(rollNumber).append(",").append(plainPassword).append("\n");
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageStudentsUI::new);
    }
}