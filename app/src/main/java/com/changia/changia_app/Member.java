package com.changia.changia_app;

public class Member {
    private String name;
    private String phone;
    private boolean isAdmin;
    private boolean hasPaid;

    public Member(String name, String phone, boolean isAdmin, boolean hasPaid) {
        this.name = name;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.hasPaid = hasPaid;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public boolean isAdmin() { return isAdmin; }
    public boolean hasPaid() { return hasPaid; }
}