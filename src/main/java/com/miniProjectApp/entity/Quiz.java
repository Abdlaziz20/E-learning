package com.miniProjectApp.entity;

import java.sql.Timestamp;

public class Quiz {
private int id;
private String title;
private String description;
private int teacherId;
private String teacherName;
private int categoryId;
private String categoryName;
private int timeLimit;
private int questionCount;
private Timestamp createdAt;

public Quiz() {}

public int getId()                     { return id; }
public void setId(int id)              { this.id = id; }
public String getTitle()               { return title; }
public void setTitle(String t)         { this.title = t; }
public String getDescription()         { return description; }
public void setDescription(String d)   { this.description = d; }
public int getTeacherId()              { return teacherId; }
public void setTeacherId(int tid)      { this.teacherId = tid; }
public String getTeacherName()         { return teacherName; }
public void setTeacherName(String n)   { this.teacherName = n; }
public int getCategoryId()             { return categoryId; }
public void setCategoryId(int cid)     { this.categoryId = cid; }
public String getCategoryName()        { return categoryName; }
public void setCategoryName(String n)  { this.categoryName = n; }
public int getTimeLimit()              { return timeLimit; }
public void setTimeLimit(int t)        { this.timeLimit = t; }
public int getQuestionCount()          { return questionCount; }
public void setQuestionCount(int q)    { this.questionCount = q; }
public Timestamp getCreatedAt()        { return createdAt; }
public void setCreatedAt(Timestamp t)  { this.createdAt = t; }

@Override public String toString() { return title; }
}