package com.chemz.lms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "teachers")
public class Teacher extends User {
    private String subject;

    public Teacher() {}
    public Teacher(String username, String password, String email, String subject) {
        super(username, password, email, "TEACHER");
        this.subject = subject;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}
