package model;


public class Librarian extends Person {

    private String employeeId;

    public Librarian(String name, String contact, String employeeId) {
        super(name, contact);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String getRole() {
        return "Librarian";
    }
}
