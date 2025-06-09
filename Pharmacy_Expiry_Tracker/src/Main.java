import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame("Pharmacy Expiry Tracker");
        JButton addBtn = new JButton("➕ Add Medicine");
        JButton checkBtn = new JButton("🔍 Check Expiry");

        addBtn.addActionListener(e -> new AddMedicine());
        checkBtn.addActionListener(e -> CheckExpiry.check());

        f.setLayout(new java.awt.FlowLayout());
        f.add(addBtn);
        f.add(checkBtn);
        f.setSize(300, 100);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
