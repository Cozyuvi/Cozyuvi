import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class ManageComplaintsUI extends JFrame {

    private JTextField searchField;
    private JTable complaintTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private List<Document> complaintsList = new ArrayList<>();

    public ManageComplaintsUI() {
        setTitle("Student Complaints - Admin View");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(245, 245, 245));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("📋 Student Complaints", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(52, 73, 94));
        titlePanel.add(title, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 28));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        searchPanel.add(new JLabel("🔍 Search: "));
        searchPanel.add(searchField);

        titlePanel.add(searchPanel, BorderLayout.EAST);

        container.add(titlePanel, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DefaultTableModel(new String[]{"ID", "Student Name", "Room", "Complaint", "Date"}, 0);
        complaintTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        complaintTable.setRowSorter(sorter);
        complaintTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        complaintTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(complaintTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        container.add(scrollPane, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(120, 35));
        backBtn.addActionListener(e -> {
            dispose();
            new DashboardUI(); // Replace this with your actual Dashboard UI
        });

        JButton deleteBtn = new JButton("🗑️ Delete Selected");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.setPreferredSize(new Dimension(160, 35));
        deleteBtn.addActionListener(e -> {
            int row = complaintTable.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete selected complaint?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        footerPanel.add(backBtn, BorderLayout.WEST);
        footerPanel.add(deleteBtn, BorderLayout.EAST);

        container.add(footerPanel, BorderLayout.SOUTH);

        // Filter Logic
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }

            private void filterTable() {
                String keyword = searchField.getText();
                sorter.setRowFilter(keyword.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + keyword));
            }
        });

        add(container);
        loadComplaintsFromDB();
        displayComplaints();

        setVisible(true);
    }

    private void loadComplaintsFromDB() {
        complaintsList.clear();
        try {
            MongoDBHelper db = new MongoDBHelper();
            MongoCollection<Document> complaintsCollection = db.getCollection("Complaints");

            try (MongoCursor<Document> cursor = complaintsCollection.find().iterator()) {
                while (cursor.hasNext()) {
                    complaintsList.add(cursor.next());
                }
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load complaints", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayComplaints() {
        tableModel.setRowCount(0);  // Clear existing rows
        int id = 1;
        for (Document complaint : complaintsList) {
            String name = complaint.getString("name");
            String room = complaint.getString("room");
            String text = complaint.getString("complaint");

            Object createdOn = complaint.get("createdOn");
            String dateString = "";
            if (createdOn instanceof Date) {
                dateString = dateFormat.format((Date) createdOn);
            } else if (createdOn instanceof String) {
                dateString = (String) createdOn;
            }

            tableModel.addRow(new Object[]{id++, name, room, text, dateString});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageComplaintsUI::new);
    }
}
