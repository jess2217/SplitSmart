package com.splitexpense.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    @Column(unique = true, nullable = false)
private String email;
    private String college;

    protected Student() {
    }

    public Student(
            String name,
            String email,
            String college) {

        this.name = name;
        this.email = email;
        this.college = college;
    }

    public Student(
            int id,
            String name,
            String email,
            String college) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.college = college;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                " | Name: " + name +
                " | Email: " + email +
                " | College: " + college;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof Student)) return false;

        Student other = (Student) obj;

        return id != 0 && id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}