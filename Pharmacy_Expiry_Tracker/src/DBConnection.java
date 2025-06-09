import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ✅ this line loads driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://localhost:3306/pharmacy"; // ✅ DB name
        String user = "root";     // ✅ your MySQL user
        String password = "root";  // ✅ put your MySQL password

        return DriverManager.getConnection(url, user, password);
    }
}
