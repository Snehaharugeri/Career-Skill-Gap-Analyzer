package com.career.model;

public class JobRole {
    private final int id;
    private final String name;
    private final String description;
    public JobRole(int id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    @Override public String toString() { return id + ". " + name + " — " + description; }
}
