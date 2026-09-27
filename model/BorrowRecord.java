package model;

public class BorrowRecord {

    private String bookTitle;
    private String memberName;
    private String dueDate;

    public BorrowRecord(String bookTitle, String memberName, String dueDate) {
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.dueDate = dueDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return bookTitle + " | borrowed by " + memberName + " | due " + dueDate;
    }
}
