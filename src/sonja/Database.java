package sonja;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wrapper class for handling database connections
 * @author ewinge
 */
public class Database implements AutoCloseable {
    private final Connection con;

    Database() throws SQLException {
	con = DriverManager.getConnection(Sonja.config.getProperty("jdbc.url"), Sonja.config);
    }

    @Override
    public void close() throws SQLException {
	if (con != null) {
	    con.close();
	}
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
	return con.prepareStatement(query);
    }

    public Statement createStatement() throws SQLException {
	return con.createStatement();
    }
}
