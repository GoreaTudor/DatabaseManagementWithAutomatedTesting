import Database.DbConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionTest {

    @Test
    public void connectionTest () {
        try {
            ResultSet resultSet = DbConnector.getStatement().executeQuery("SELECT * FROM user_table");

            Assertions.assertTrue(resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
