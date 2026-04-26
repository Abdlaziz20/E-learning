package com.miniProjectApp.entity;

public class Category {
    private int id;
    private String name;
    private String description;
    private String icon;

    public Category() {}
    public Category(int id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }
    public String getName()          { return name; }
    public void setName(String n)    { this.name = n; }
    public String getDescription()   { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getIcon()          { return icon; }
    public void setIcon(String i)    { this.icon = i; }

    @Override public String toString() { return name; }
}