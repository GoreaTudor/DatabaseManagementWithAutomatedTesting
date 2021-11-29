package UserTests;

import Database.DbConnector;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryTests {
    private static InventoryTests instance = new InventoryTests();
    private ResultSet rs;

    private InventoryTests () {}
    public static InventoryTests getInstance() {
        return instance;
    }

    private static Stream<Arguments> newInvData () {
        return Stream.of(
                Arguments.of("_dummy1_", 1, 2),
                Arguments.of("_dummy1_", 2, 19),
                Arguments.of("_dummy1_", 3, 2),
                Arguments.of("_dummy2_", 4, 22),
                Arguments.of("_dummy2_", 5, 23),
                Arguments.of("_dummy3_", 6, 44),
                Arguments.of("_dummy3_", 7, 32),
                Arguments.of("_dummy3_", 8, 19),
                Arguments.of("_dummy4_", 9, 2),
                Arguments.of("_dummy4_", 10, 5),
                Arguments.of("_dummy5_", 11, 17)
        );
    }


    ///// TABLE OPS /////
    @Test
    @Order(1)
    @DisplayName("1: table name should be: inventory")
    public void tableNameCheck () {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM inventory;");

            Assertions.assertTrue(rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @Order(2)
    @DisplayName("2: column names should be: username, item_id, quantity")
    public void columnNameCheck () {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT username, item_id, quantity FROM inventory;");

            Assertions.assertTrue(rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(3)
    @DisplayName("3: should insert a new test inventory data")
    @MethodSource("newInvData")
    public void shouldInsertANewItem (String username, int id, int quantity) {
        try {
            // Add a new inventory data
            DbConnector.getStatement().executeUpdate("INSERT INTO inventory VALUES ('" + username + "', " + id + ", " + quantity + ");");

            // Verify existence of inventory data
            rs = DbConnector.getStatement().executeQuery(
                    "SELECT * FROM inventory WHERE username = '" + username + "' AND item_id = " + id + ";"
            );
            Assertions.assertTrue(rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(4)
    @DisplayName("4: should delete the inserted test inventory data")
    @MethodSource("newInvData")
    public void shouldDeleteAllTestItems (String username, int id, int quantity) {
        try {
            rs = DbConnector.getStatement().executeQuery(
                    "SELECT * FROM inventory WHERE username = '" + username + "' AND item_id = " + id + ";"
            );
            if(!rs.next())
                Assertions.fail();

            DbConnector.getStatement().executeUpdate(
                    "DELETE FROM inventory WHERE username = '" + username + "' AND item_id = " + id + ";"
            );

            rs = DbConnector.getStatement().executeQuery(
                    "SELECT * FROM inventory WHERE username = '" + username + "' AND item_id = " + id + ";"
            );
            Assertions.assertFalse(rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(5)
    @DisplayName("5: should not allow new item insertion because of PK constraint")
    @MethodSource("newInvData")
    public void shouldThrowPKException (String username, int id, int quantity) {
        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            // (username and item_id) is the composite PK for item_table
            DbConnector.getStatement().executeUpdate("INSERT INTO inventory VALUES ('" + username + "', " + id + ", " + quantity + ");");
            DbConnector.getStatement().executeUpdate("INSERT INTO inventory VALUES ('" + username + "', " + id + ", 10);");
        });

        try {
            DbConnector.getStatement().executeUpdate(
                    "DELETE FROM inventory WHERE username = '" + username + "' AND item_id = " + id + ";"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
