package model;

public class Member extends Person {

    private int id;

    public Member(int id, String name, String contact) {
        super(name, contact);
        this.id = id;
    }

    public Member(String name, String contact) {
        super(name, contact);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getRole() {
        return "Member";
    }
}
