import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class CheckExpiry {
    public static void check() {
        StringBuilder sb = new StringBuilder();
        LocalDate today = LocalDate.now();

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM medicines WHERE expiry_date <= ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(today.plusDays(30)));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String batch = rs.getString("batch");
                String expiry = rs.getDate("expiry_date").toString();
                int qty = rs.getInt("quantity");

                sb.append("Name: ").append(name).append("\n");
                sb.append("Batch: ").append(batch).append("\n");
                sb.append("Expiry: ").append(expiry).append("\n");
                sb.append("Qty: ").append(qty).append("\n");
                sb.append("-------------------------\n");
            }

            if (sb.length() == 0) {
                JOptionPane.showMessageDialog(null, "No medicines are near expiry.");
            } else {
                JOptionPane.showMessageDialog(null, "Near Expiry Medicines:\n\n" + sb.toString());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
}
