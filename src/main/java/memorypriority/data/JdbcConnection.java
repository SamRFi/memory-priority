package memorypriority.data;

import memorypriority.util.Config;
import memorypriority.util.Crypto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class JdbcConnection {
    public static final String MYSQL_URL;
    public static final String USERNAME;
    public static final String PASSWORD;
    private static String url;

    private JdbcConnection() {
    }


    static {
        USERNAME = Crypto.getInstance().decrypt(Config.getInstance().readSetting("dbUsername"));
        PASSWORD = Crypto.getInstance().decrypt(Config.getInstance().readSetting("dbPassword"));
        MYSQL_URL = Config.getInstance().readSetting("mySqlUrl");
        url = MYSQL_URL;
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, USERNAME, PASSWORD);
    }

    public static void setConnection(String url) {
        Objects.requireNonNull(url);
        JdbcConnection.url = url;
    }

}