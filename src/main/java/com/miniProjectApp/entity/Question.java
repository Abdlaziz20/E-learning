package com.miniProjectApp.entity;

public class Question {
    private int id;
    private int quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // A, B, C, D
    private int points;
    private int orderNum;

    public Question() {}

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }
    public int getQuizId()                  { return quizId; }
    public void setQuizId(int qid)          { this.quizId = qid; }
    public String getQuestionText()         { return questionText; }
    public void setQuestionText(String q)   { this.questionText = q; }
    public String getOptionA()              { return optionA; }
    public void setOptionA(String a)        { this.optionA = a; }
    public String getOptionB()              { return optionB; }
    public void setOptionB(String b)        { this.optionB = b; }
    public String getOptionC()              { return optionC; }
    public void setOptionC(String c)        { this.optionC = c; }
    public String getOptionD()              { return optionD; }
    public void setOptionD(String d)        { this.optionD = d; }
    public String getCorrectOption()        { return correctOption; }
    public void setCorrectOption(String co) { this.correctOption = co; }
    public int getPoints()                  { return points; }
    public void setPoints(int p)            { this.points = p; }
    public int getOrderNum()                { return orderNum; }
    public void setOrderNum(int o)          { this.orderNum = o; }

    public String getOptionByLetter(String letter) {
        switch(letter.toUpperCase()) {
            case "A": return optionA;
            case "B": return optionB;
            case "C": return optionC;
            case "D": return optionD;
            default: return "";
        }
    }
}