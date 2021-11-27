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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {

    private static UserTests instance = new UserTests();
    public ResultSet rs;

    private UserTests () {}
    public static UserTests getInstance() {
        return instance;
    }

    private static Stream <Arguments> provideDataForNewUser () {
        return Stream.of(
                Arguments.of("_dummy1_", new char[]{'1', '2', '3', '4'}, false),
                Arguments.of("_dummy2_", new char[]{'2', '3', '4', '5'}, true),
                Arguments.of("_dummy3_", new char[]{'3', '4', '5', '6'}, true),
                Arguments.of("_dummy4_", new char[]{'4', '5', '6', '7'}, false),
                Arguments.of("_dummy5_", new char[]{'5', '6', '7', '8'}, false)
        );
    }


    ///// TABLE OPS /////
    @Test
    @Order(1)
    @DisplayName("1: table name should be: user_table")
    public void tableNameCheck () {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table");

            Assertions.assertTrue(rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @Order(2)
    @DisplayName("2: column names should be: username, password and isAdmin")
    public void columnNameCheck () {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT username, password, isAdmin FROM user_table;");

            Assertions.assertTrue(rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(3)
    @DisplayName("3: should insert a new test user")
    @MethodSource("provideDataForNewUser")
    public void shouldInsertANewUser (String username, char[] password, boolean isAdmin) {
        int pwd = Arrays.hashCode(password);

        try {
            // Add a new user
            DbConnector.getStatement().executeUpdate("INSERT INTO user_table VALUES ('" + username + "', " + pwd + ", " + isAdmin + ");");

            // Verify existence of user
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table WHERE username = '" + username + "';");
            Assertions.assertTrue(rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(4)
    @DisplayName("4: should delete the inserted test users")
    @MethodSource("provideDataForNewUser")
    public void shouldDeleteAllTestUsers (String username, char[] password, boolean isAdmin) {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table WHERE username = '" + username + "';");
            Assertions.assertFalse(!rs.next());

            DbConnector.getStatement().executeUpdate("DELETE FROM user_table WHERE username = '" + username + "';");

            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table WHERE username = '" + username + "';");
            Assertions.assertEquals(false, rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @ParameterizedTest
    @Order(5)
    @DisplayName("5: should not allow new user insertion because of PK constraint")
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
