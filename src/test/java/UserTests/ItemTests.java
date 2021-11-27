package UserTests;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.provider.Arguments;

import java.sql.ResultSet;
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
                Arguments.of("_item1_", "_desc1_"),
                Arguments.of("_item2_", "_desc2_"),
                Arguments.of("_item3_", "_desc3_"),
                Arguments.of("_item4_", "_desc4_"),
                Arguments.of("_item5_", "_desc5_")
        );
    }


}
