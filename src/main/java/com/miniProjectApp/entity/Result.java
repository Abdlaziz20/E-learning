package com.miniProjectApp.entity;

import java.sql.Timestamp;

public class Result {
    private int id;
    private int studentId;
    private String studentName;
    private int quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private int timeTaken;
    private Timestamp takenAt;

    public Result() {}

    public int getId()                     { return id; }
    public void setId(int id)              { this.id = id; }
    public int getStudentId()              { return studentId; }
    public void setStudentId(int sid)      { this.studentId = sid; }
    public String getStudentName()         { return studentName; }
    public void setStudentName(String n)   { this.studentName = n; }
    public int getQuizId()                 { return quizId; }
    public void setQuizId(int qid)         { this.quizId = qid; }
    public String getQuizTitle()           { return quizTitle; }
    public void setQuizTitle(String t)     { this.quizTitle = t; }
    public int getScore()                  { return score; }
    public void setScore(int s)            { this.score = s; }
    public int getTotalQuestions()         { return totalQuestions; }
    public void setTotalQuestions(int t)   { this.totalQuestions = t; }
    public int getCorrectAnswers()         { return correctAnswers; }
    public void setCorrectAnswers(int ca)  { this.correctAnswers = ca; }
    public int getTimeTaken()              { return timeTaken; }
    public void setTimeTaken(int t)        { this.timeTaken = t; }
    public Timestamp getTakenAt()          { return takenAt; }
    public void setTakenAt(Timestamp t)    { this.takenAt = t; }

    public int getPercentage() {
        if (totalQuestions == 0) return 0;
        return (correctAnswers * 100) / totalQuestions;
    }

    public String getGrade() {
        int pct = getPercentage();
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }
}
