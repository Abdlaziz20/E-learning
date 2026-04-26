package com.miniProjectApp.entity;

public class Teacher extends User {
    public Teacher() { setRole(Role.TEACHER); }
    public Teacher(String fullName, String email, String password) {
        super(fullName, email, password, Role.TEACHER);
    }
}