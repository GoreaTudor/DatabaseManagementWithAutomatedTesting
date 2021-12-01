package TableTests;

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
public class ItemTests {

    private static ItemTests instance = new ItemTests();
    private ResultSet rs;

    private ItemTests () {}
    public static ItemTests getInstance() {
        return instance;
    }

    private static Stream <Arguments> provideDataForNewItem () {
        return Stream.of(
                Arguments.of(-1, "_item1_", "_desc1_"),
                Arguments.of(-2, "_item2_", "_desc2_"),
                Arguments.of(-3, "_item3_", "_desc3_"),
                Arguments.of(-4, "_item4_", "_desc4_"),
                Arguments.of(-5, "_item5_", "_desc5_")
        );
    }


    ///// TABLE OPS /////
    @Test
    @Order(1)
    @DisplayName("1: table name should be: item_table")
    public void tableNameCheck () {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM item_table;");

            Assertions.assertTrue(rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    @Order(2)
    @DisplayName("2: column names should be: item_id, item_name, item_description")
    public void columnNameCheck () {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT item_id, item_name, item_description FROM item_table;");

            Assertions.assertTrue(rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(3)
    @DisplayName("3: should insert a new test item")
    @MethodSource("provideDataForNewItem")
    public void shouldInsertANewItem (int id, String name, String desc) {
        try {
            // Add a new item
            DbConnector.getStatement().executeUpdate("INSERT INTO item_table VALUES (" + id + ", '" + name + "', '" + desc + "');");

            // Verify existence of item
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM item_table WHERE item_id = " + id);
            Assertions.assertTrue(rs.next());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(4)
    @DisplayName("4: should delete the inserted test items")
    @MethodSource("provideDataForNewItem")
    public void shouldDeleteAllTestItems (int id, String name, String desc) {
        try {
            rs = DbConnector.getStatement().executeQuery("SELECT * FROM item_table WHERE item_id = " + id + ";");
            if(!rs.next())
                Assertions.fail();

            DbConnector.getStatement().executeUpdate("DELETE FROM item_table WHERE item_id = " + id + ";");

            rs = DbConnector.getStatement().executeQuery("SELECT * FROM item_table WHERE item_id = " + id + ";");
            Assertions.assertFalse(rs.next());

        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @Order(5)
    @DisplayName("5: should not allow new item insertion because of PK constraint")
    @MethodSource("provideDataForNewItem")
    public void shouldThrowPKException (int id, String name, String desc) {
        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            // item_id is PK for item_table
            DbConnector.getStatement().executeUpdate("INSERT INTO item_table VALUES (" + id + ", '" + name + "', '" + desc + "');");
            DbConnector.getStatement().executeUpdate("INSERT INTO item_table VALUES (" + id + ", '_name_', '_desc_');");
        });

        try {
            DbConnector.getStatement().executeUpdate("DELETE FROM item_table WHERE item_id = " + id + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    ///// VIEWS /////
    // No view found to test;


    ///// STORED FUNCTIONS AND PROCEDURES /////
    @ParameterizedTest
    @Order(6)
    @DisplayName("6: should call newItem() procedure")
    @MethodSource("provideDataForNewItem")
    public void shouldCallNewItem (int id, String name, String desc) {
        try {
            // newItem(String, String)
            DbConnector.getStatement().executeUpdate("CALL newItem ('" + name + "', '" + desc + "');");

            rs = DbConnector.getStatement().executeQuery(
                    "SELECT * FROM item_table WHERE item_name = '" + name + "' AND item_description = '" + desc + "';"
            );
            Assertions.assertTrue(rs.next());

            DbConnector.getStatement().executeUpdate(
                    "DELETE FROM item_table WHERE item_name = '" + name + "' AND item_description = '" + desc + "';"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
