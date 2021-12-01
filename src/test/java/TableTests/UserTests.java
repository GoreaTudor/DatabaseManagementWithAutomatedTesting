package TableTests;

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
    private ResultSet rs;

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
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table;");

            Assertions.assertTrue(rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @Order(2)
    @DisplayName("2: column names should be: username, password, isAdmin")
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
            if(!rs.next()) // fail if there is nothing to delete
                Assertions.fail();

            DbConnector.getStatement().executeUpdate("DELETE FROM user_table WHERE username = '" + username + "';");

            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table WHERE username = '" + username + "';");
            Assertions.assertFalse(rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(5)
    @DisplayName("5: should not allow new user insertion because of PK constraint")
    @MethodSource("provideDataForNewUser")
    public void shouldThrowPKException (String username, char[] password, boolean isAdmin) {
        int pwd = Arrays.hashCode(password);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            // username is the primary key for user_table
            DbConnector.getStatement().executeUpdate("CALL newUser('" + username + "', " + pwd + ", " + isAdmin + ");");
            DbConnector.getStatement().executeUpdate("CALL newUser('" + username + "', 1111, false);");
        });

        try {
            DbConnector.getStatement().executeUpdate("DELETE FROM user_table WHERE username = '" + username + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    ///// VIEWS /////
    @Test
    @Order(6)
    @DisplayName("6: users view (username, password) - users only")
    public void shouldSelectUsersView () {
        try {
            rs = DbConnector.getStatement().executeQuery(
                    "SELECT users.username, isAdmin FROM users INNER JOIN user_table ON user_table.username = users.username;"
            );

            while (rs.next()) {
                if (!rs.getString("isAdmin").equals("0"))
                    throw new SQLException("admin found inside users table");
            }

            rs = DbConnector.getStatement().executeQuery("SELECT * FROM users");
            Assertions.assertTrue(rs.findColumn("username") == 1 && rs.findColumn("password") == 2);


        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @Order(7)
    @DisplayName("7: admins view (username, password) - admins only")
    public void shouldSelectAdminsView () {
        try {
            rs = DbConnector.getStatement().executeQuery(
                    "SELECT admins.username, isAdmin FROM admins INNER JOIN user_table ON user_table.username = admins.username;"
            );

            while (rs.next()) {
                if (!rs.getString("isAdmin").equals("1"))
                    throw new SQLException("admin found inside users table");
            }

            rs = DbConnector.getStatement().executeQuery("SELECT * FROM admins");
            Assertions.assertTrue(rs.findColumn("username") == 1 && rs.findColumn("password") == 2);

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }


    ///// STORED FUNCTIONS AND PROCEDURES /////
    @ParameterizedTest
    @Order(8)
    @DisplayName("8: should call newUser() procedure")
    @MethodSource("provideDataForNewUser")
    public void shouldCallNewUser (String username, char[] password, boolean isAdmin) {
        int pwd = Arrays.hashCode(password);
        try {
            // newUser(String, int, tinyint <=> boolean)
            DbConnector.getStatement().executeUpdate("CALL newUser ('" + username + "', " + pwd + ", " + isAdmin + ");");

            rs = DbConnector.getStatement().executeQuery("SELECT * FROM user_table WHERE username = '" + username + "';");
            Assertions.assertTrue(rs.next());

            DbConnector.getStatement().executeUpdate("DELETE FROM user_table WHERE username = '" + username + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

}
