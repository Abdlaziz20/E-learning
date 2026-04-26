package com.miniProjectApp.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import   com.miniProjectApp.entity.*;
import com.miniProjectApp.util.DBConnection;

public class ResultDAO {

    public void insert(Result r) throws SQLException {
        String sql = "INSERT INTO results (student_id, quiz_id, score, total_questions, correct_answers, time_taken) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getStudentId());
            ps.setInt(2, r.getQuizId());
            ps.setInt(3, r.getScore());
            ps.setInt(4, r.getTotalQuestions());
            ps.setInt(5, r.getCorrectAnswers());
            ps.setInt(6, r.getTimeTaken());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) r.setId(keys.getInt(1));
        }
    }

    public List<Result> findByStudent(int studentId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS student_name, q.title AS quiz_title " +
                "FROM results r JOIN users u ON r.student_id=u.id " +
                "JOIN quizzes q ON r.quiz_id=q.id " +
                "WHERE r.student_id=? ORDER BY r.taken_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Result> findByQuiz(int quizId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS student_name, q.title AS quiz_title " +
                "FROM results r JOIN users u ON r.student_id=u.id " +
                "JOIN quizzes q ON r.quiz_id=q.id " +
                "WHERE r.quiz_id=? ORDER BY r.score DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Result map(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStudentName(rs.getString("student_name"));
        r.setQuizId(rs.getInt("quiz_id"));
        r.setQuizTitle(rs.getString("quiz_title"));
        r.setScore(rs.getInt("score"));
        r.setTotalQuestions(rs.getInt("total_questions"));
        r.setCorrectAnswers(rs.getInt("correct_answers"));
        r.setTimeTaken(rs.getInt("time_taken"));
        r.setTakenAt(rs.getTimestamp("taken_at"));
        return r;
    }
}
