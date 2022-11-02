package my.virkato.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


final public class ConnectionUtils {

    private static String hostNameMySql = "localhost";
    private static String dbNameMySql = "first";
    private static String userNameMySql = "root";
    private static String passwordMySql = "root";

    public static String welcome() {
        return "Модуль для работы с базами данных";
    }

    public static Connection getMySqlConnection(String hostName, String dbName, String userName, String password) {
        hostNameMySql = hostName;
        dbNameMySql = dbName;
        userNameMySql = userName;
        passwordMySql = password;
        try {
            return getMySqlConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getMySqlConnection() throws SQLException, ClassNotFoundException {
        String connectionURL = "jdbc:mysql://" + hostNameMySql + ":3306/" + dbNameMySql;

        // Declare the class Driver for MySQL DB
        // This is necessary with Java 5 (or older)
        // Java6 (or newer) automatically find the appropriate driver.
        // If you use Java> 5, then this line is not needed.
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(connectionURL, userNameMySql,
                passwordMySql);
    }
}
