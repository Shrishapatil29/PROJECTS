import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class AddMedicine extends JFrame {
    public AddMedicine() {
        setTitle("Add Medicine");
        setSize(350, 300);
        setLayout(new GridLayout(6, 2, 10, 10));

        JLabel nameLabel = new JLabel("Medicine Name:");
        JTextField nameField = new JTextField();

        JLabel batchLabel = new JLabel("Batch No:");
        JTextField batchField = new JTextField();

        JLabel expiryLabel = new JLabel("Expiry Date (YYYY-MM-DD):");
        JTextField expiryField = new JTextField();

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField();

        JButton saveButton = new JButton("Save");

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String batch = batchField.getText();
            String expiry = expiryField.getText();
            String qtyStr = qtyField.getText();

            try (Connection con = DBConnection.getConnection()) {
                String sql = "INSERT INTO medicines (name, batch, expiry_date, quantity) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, batch);
                ps.setDate(3, Date.valueOf(expiry));
                ps.setInt(4, Integer.parseInt(qtyStr));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Medicine added successfully!");

                // Check if near expiry
                LocalDate exp = LocalDate.parse(expiry);
                LocalDate today = LocalDate.now();
                if (exp.minusDays(30).isBefore(today)) {
                    JOptionPane.showMessageDialog(this, "⚠ Warning: This medicine will expire soon!");
                }

                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(nameLabel); add(nameField);
        add(batchLabel); add(batchField);
        add(expiryLabel); add(expiryField);
        add(qtyLabel); add(qtyField);
        add(new JLabel()); add(saveButton);

        setVisible(true);
    }
}
