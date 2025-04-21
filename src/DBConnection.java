import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/payroll_db";
    private static final String USER = "root";  
    private static final String PASSWORD = "1234";  

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL Driver
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver Not Found! Add MySQL Connector JAR.");
        } catch (SQLException e) {
            System.out.println("Database Connection Failed! Error: " + e.getMessage());
        }
        return null;
    }
}
