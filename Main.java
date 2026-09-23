package main;

import dao.BookDAO;
import dao.MemberDAO;
import exception.BookNotAvailableException;
import exception.RecordNotFoundException;
import model.Book;
import model.BorrowRecord;
import model.Librarian;
import model.Member;
import service.LibraryService;
import util.DBConnection;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static BookDAO bookDAO = new BookDAO();
    static MemberDAO memberDAO = new MemberDAO();
    static LibraryService libraryService = new LibraryService();

    public static void main(String[] args) {

        DBConnection.setupDatabase();

        // shows the abstract Person / polymorphism in action
        Librarian librarian = new Librarian("Mrs. Sharma", "9800000000", "EMP001");
        System.out.println("Welcome! " + librarian.getRole() + " on duty: " + librarian.getName());

        boolean running = true;

        while (running) {
            printMenu();
            String input = sc.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please type a number from the menu.");
                continue;
            }

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    viewAllBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    deleteBook();
                    break;
                case 6:
                    addMember();
                    break;
                case 7:
                    viewAllMembers();
                    break;
                case 8:
                    issueBook();
                    break;
                case 9:
                    returnBook();
                    break;
                case 10:
                    viewOverdueBooks();
                    break;
                case 11:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }

        sc.close();
    }

    static void printMenu() {
        System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Book by Title");
        System.out.println("4. Update Book");
        System.out.println("5. Delete Book");
        System.out.println("6. Add Member");
        System.out.println("7. View All Members");
        System.out.println("8. Issue Book to Member");
        System.out.println("9. Return Book");
        System.out.println("10. View Overdue Books Report");
        System.out.println("11. Exit");
        System.out.print("Choose an option: ");
    }

    // keeps asking until the user types a real number
    static int readInt(String message) {
        int value = -1;
        boolean valid = false;
        while (!valid) {
            System.out.print(message);
            String line = sc.nextLine();
            try {
                value = Integer.parseInt(line.trim());
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid number, try again.");
            }
        }
        return value;
    }

    static void addBook() {
        System.out.print("Title: ");
        String title = sc.nextLine();
        System.out.print("Author: ");
        String author = sc.nextLine();
        System.out.print("ISBN: ");
        String isbn = sc.nextLine();
        System.out.print("Category: ");
        String category = sc.nextLine();
        int copies = readInt("Total Copies: ");

        Book book = new Book(title, author, isbn, category, copies);

        try {
            bookDAO.addBook(book);
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            System.out.println("Could not add book. Reason: " + e.getMessage());
        }
    }

    static void viewAllBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("No books in the library yet.");
            }
            for (Book book : books) {
                System.out.println(book);
            }
        } catch (SQLException e) {
            System.out.println("Could not load books. Reason: " + e.getMessage());
        }
    }

    static void searchBook() {
        System.out.print("Enter title keyword: ");
        String keyword = sc.nextLine();
        try {
            List<Book> results = bookDAO.searchByTitle(keyword);
            if (results.isEmpty()) {
                System.out.println("No matching books found.");
            }
            for (Book book : results) {
                System.out.println(book);
            }
        } catch (SQLException e) {
            System.out.println("Search failed. Reason: " + e.getMessage());
        }
    }

    static void updateBook() {
        int id = readInt("Enter Book ID to update: ");
        try {
            Book book = bookDAO.getBookById(id);
            if (book == null) {
                System.out.println("No book found with that ID.");
                return;
            }
            System.out.print("New Title (" + book.getTitle() + ", press Enter to keep): ");
            String title = sc.nextLine();
            if (!title.isEmpty()) {
                book.setTitle(title);
            }
            System.out.print("New Author (" + book.getAuthor() + ", press Enter to keep): ");
            String author = sc.nextLine();
            if (!author.isEmpty()) {
                book.setAuthor(author);
            }
            bookDAO.updateBook(book);
            System.out.println("Book updated.");
        } catch (SQLException e) {
            System.out.println("Update failed. Reason: " + e.getMessage());
        }
    }

    static void deleteBook() {
        int id = readInt("Enter Book ID to delete: ");
        try {
            bookDAO.deleteBook(id);
            System.out.println("Book deleted (if it existed).");
        } catch (SQLException e) {
            System.out.println("Delete failed. Reason: " + e.getMessage());
        }
    }

    static void addMember() {
        System.out.print("Member Name: ");
        String name = sc.nextLine();
        System.out.print("Contact: ");
        String contact = sc.nextLine();
        Member member = new Member(name, contact);
        try {
            memberDAO.addMember(member);
            System.out.println("Member added successfully.");
        } catch (SQLException e) {
            System.out.println("Could not add member. Reason: " + e.getMessage());
        }
    }

    static void viewAllMembers() {
        try {
            List<Member> members = memberDAO.getAllMembers();
            if (members.isEmpty()) {
                System.out.println("No members registered yet.");
            }
            for (Member member : members) {
                System.out.println(member.getId() + " | " + member.getName() + " | " + member.getContact());
            }
        } catch (SQLException e) {
            System.out.println("Could not load members. Reason: " + e.getMessage());
        }
    }

    static void issueBook() {
        int bookId = readInt("Book ID: ");
        int memberId = readInt("Member ID: ");
        try {
            libraryService.issueBook(bookId, memberId);
            System.out.println("Book issued successfully.");
        } catch (BookNotAvailableException | RecordNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Something went wrong. Reason: " + e.getMessage());
        }
    }

    static void returnBook() {
        int bookId = readInt("Book ID: ");
        int memberId = readInt("Member ID: ");
        try {
            libraryService.returnBook(bookId, memberId);
            System.out.println("Book returned successfully.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Something went wrong. Reason: " + e.getMessage());
        }
    }

    static void viewOverdueBooks() {
        try {
            List<BorrowRecord> overdue = libraryService.getOverdueBooks();
            if (overdue.isEmpty()) {
                System.out.println("No overdue books right now.");
            }
            for (BorrowRecord record : overdue) {
                System.out.println(record);
            }
        } catch (SQLException e) {
            System.out.println("Could not load report. Reason: " + e.getMessage());
        }
    }
}
