package UserTests;

import Database.DbConnector;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.stream.Stream;

public class UserTests {

    private static UserTests instance = new UserTests();
    public ResultSet rs;

    private UserTests () {}
    public static UserTests getInstance() {
        return instance;
    }

    private static Stream <Arguments> provideDataForNewUser () {
        return Stream.of(
                Arguments.of("dummy1", new char[]{'1', '2', '3', '4'}, false),
                Arguments.of("dummy2", new char[]{'2', '3', '4', '5'}, true),
                Arguments.of("dummy3", new char[]{'3', '4', '5', '6'}, true),
                Arguments.of("dummy4", new char[]{'4', '5', '6', '7'}, false),
                Arguments.of("dummy5", new char[]{'5', '6', '7', '8'}, false)
        );
    }


    ///// TABLE OPS /////
    @BeforeAll
    @DisplayName("Table name should be user_table")
    public static void tableNameCheck () {
        try {
            getInstance().rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table");

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    @DisplayName("should select username, password and isAdmin")
    public void shouldSelectAll () {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT username, password, isAdmin FROM user_table;");

            Assertions.assertTrue(rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @DisplayName("should insert a new user into user_table using INSERT, then delete the user")
    @MethodSource("provideDataForNewUser")
    public void shouldInsertANewUser (String username, char[] password, boolean isAdmin) {
        int pwd = Arrays.hashCode(password);

        try {
            // Add a new user
            DbConnector.getStatement().executeUpdate("INSERT INTO user_table VALUES ('" + username + "', " + pwd + ", " + isAdmin + ");");

            // Verify existence of user
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table WHERE username = '" + username + "';");
            Assertions.assertTrue(rs.next());

            // Delete user
            DbConnector.getStatement().executeUpdate("DELETE FROM user_table WHERE username = '" + username + "';");

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @DisplayName("should throw an SQLIntegrityConstraintViolationException")
    @MethodSource("provideDataForNewUser")
    public  void shouldThrowPKException (String username, char[] password, boolean isAdmin) {
        int pwd = Arrays.hashCode(password);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            DbConnector.getStatement().executeUpdate("CALL newUser('" + username + "', " + pwd + ", " + isAdmin + ");");
            DbConnector.getStatement().executeUpdate("CALL newUser('" + username + "', " + pwd + ", " + isAdmin + ");");
        });

        try {
            DbConnector.getStatement().executeUpdate("DELETE FROM user_table WHERE username = '" + username + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
