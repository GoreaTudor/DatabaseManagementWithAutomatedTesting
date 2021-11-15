package Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class DbConnector {

    private static DbConnector instance = new DbConnector();

    private Connection connection;
    private Statement statement;

    private DbConnector () {
        try {
            Scanner scanner = new Scanner(new File("src/java/Database/dbinfo.txt"));

            connection = DriverManager.getConnection(
                    scanner.next(), // url
                    scanner.next(), // user
                    scanner.next()  // password
            );

            statement = connection.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DbConnector getInstance() {
        return instance;
    }

    public static Statement getStatement() {
        return instance.statement;
    }
}
