package com.miniProjectApp.entity;

public class Student extends User {
    public Student() { setRole(Role.STUDENT); }
    public Student(String fullName, String email, String password) {
        super(fullName, email, password, Role.STUDENT);
    }
}