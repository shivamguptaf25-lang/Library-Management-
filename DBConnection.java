package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:library.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }


    public static void setupDatabase() {

        String createBooks = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "isbn TEXT," +
                "category TEXT," +
                "total_copies INTEGER NOT NULL," +
                "available_copies INTEGER NOT NULL)";

        String createMembers = "CREATE TABLE IF NOT EXISTS members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "contact TEXT)";

        String createBorrowRecords = "CREATE TABLE IF NOT EXISTS borrow_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "book_id INTEGER NOT NULL," +
                "member_id INTEGER NOT NULL," +
                "issue_date TEXT NOT NULL," +
                "due_date TEXT NOT NULL," +
                "return_date TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createBooks);
            stmt.execute(createMembers);
            stmt.execute(createBorrowRecords);

            System.out.println("Database is ready.");

        } catch (SQLException e) {
            System.out.println("Could not set up the database: " + e.getMessage());
        }
    }
}
