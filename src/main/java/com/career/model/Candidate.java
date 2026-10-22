package com.career.model;

public class Candidate {
    private int id;
    private String fullName;
    private String email;
    private String education;

    public Candidate(int id, String fullName, String email, String education) {
        this.id = id; this.fullName = fullName; this.email = email; this.education = education;
    }
    public Candidate(String fullName, String email, String education) {
        this(0, fullName, email, education);
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getEducation() { return education; }
}
