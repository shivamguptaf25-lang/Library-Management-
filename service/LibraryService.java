package service;

import dao.BookDAO;
import exception.BookNotAvailableException;
import exception.RecordNotFoundException;
import model.Book;
import model.BorrowRecord;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryService {

    private BookDAO bookDAO = new BookDAO();

    private Map<Integer, Book> bookCache = new HashMap<>();

    public void refreshCache() throws SQLException {
        bookCache.clear();
        List<Book> allBooks = bookDAO.getAllBooks();
        for (Book book : allBooks) {
            bookCache.put(book.getId(), book);
        }
    }

    public void issueBook(int bookId, int memberId)
            throws SQLException, BookNotAvailableException, RecordNotFoundException {

        Book book = bookDAO.getBookById(bookId);

        if (book == null) {
            throw new RecordNotFoundException("No book found with ID " + bookId);
        }

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(
                    "\"" + book.getTitle() + "\" has no copies available right now.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDAO.updateBook(book);

        String sql = "INSERT INTO borrow_records (book_id, member_id, issue_date, due_date) " +
                "VALUES (?, ?, date('now'), date('now', '+14 days'))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ps.executeUpdate();
        }
    }

    public void returnBook(int bookId, int memberId) throws SQLException, RecordNotFoundException {

        Book book = bookDAO.getBookById(bookId);

        if (book == null) {
            throw new RecordNotFoundException("No book found with ID " + bookId);
        }

        if (book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookDAO.updateBook(book);
        }

        String sql = "UPDATE borrow_records SET return_date = date('now') " +
                "WHERE book_id = ? AND member_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ps.executeUpdate();
        }
    }

    public List<BorrowRecord> getOverdueBooks() throws SQLException {

        List<BorrowRecord> overdueList = new ArrayList<>();

        String sql = "SELECT b.title, m.name, r.due_date " +
                "FROM borrow_records r " +
                "JOIN books b ON r.book_id = b.id " +
                "JOIN members m ON r.member_id = m.id " +
                "WHERE r.return_date IS NULL AND r.due_date < date('now')";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord(
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getString("due_date")
                );
                overdueList.add(record);
            }
        }

        overdueList.sort(new Comparator<BorrowRecord>() {
            @Override
            public int compare(BorrowRecord r1, BorrowRecord r2) {
                return r1.getDueDate().compareTo(r2.getDueDate());
            }
        });

        return overdueList;
    }
}
