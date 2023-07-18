package memorypriority.data;

import memorypriority.util.Config;
import memorypriority.util.Crypto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class JdbcConnection {

    public static final String MSSQL_URL;
    private static String url;

    private JdbcConnection() {
    }

    static {
        MSSQL_URL = Config.getInstance().readSetting("msSqlUrl");
        url = MSSQL_URL;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public static void setConnection(String url) {
        Objects.requireNonNull(url);
        JdbcConnection.url = url;
    }
}