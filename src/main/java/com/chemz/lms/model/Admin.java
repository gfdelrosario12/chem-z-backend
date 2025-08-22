package com.chemz.lms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {
    private String department;

    public Admin() {}
    public Admin(String username, String password, String email, String department) {
        super(username, password, email, "ADMIN");
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
