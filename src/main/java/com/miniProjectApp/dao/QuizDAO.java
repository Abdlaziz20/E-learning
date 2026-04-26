package com.miniProjectApp.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import  com.miniProjectApp.entity.*;
import  com.miniProjectApp.util.DBConnection;

public class QuizDAO {

    public List<Quiz> findAll() throws SQLException {
        return queryQuizzes("SELECT q.*, u.full_name AS teacher_name, c.name AS cat_name, " +
                "(SELECT COUNT(*) FROM questions WHERE quiz_id = q.id) AS qcount " +
                "FROM quizzes q JOIN users u ON q.teacher_id=u.id " +
                "JOIN categories c ON q.category_id=c.id ORDER BY q.created_at DESC", null);
    }

    public List<Quiz> findByTeacher(int teacherId) throws SQLException {
        return queryQuizzes("SELECT q.*, u.full_name AS teacher_name, c.name AS cat_name, " +
                        "(SELECT COUNT(*) FROM questions WHERE quiz_id = q.id) AS qcount " +
                        "FROM quizzes q JOIN users u ON q.teacher_id=u.id " +
                        "JOIN categories c ON q.category_id=c.id WHERE q.teacher_id=? ORDER BY q.created_at DESC",
                teacherId);
    }

    public List<Quiz> findByTeacherAndCategory(int teacherId, int categoryId) throws SQLException {
        String sql = "SELECT q.*, u.full_name AS teacher_name, c.name AS cat_name, " +
                "(SELECT COUNT(*) FROM questions WHERE quiz_id = q.id) AS qcount " +
                "FROM quizzes q JOIN users u ON q.teacher_id=u.id " +
                "JOIN categories c ON q.category_id=c.id " +
                "WHERE q.teacher_id=? AND q.category_id=? ORDER BY q.created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, categoryId);
            return mapList(ps.executeQuery());
        }
    }

    public List<Quiz> findByCategory(int categoryId) throws SQLException {
        return queryQuizzes("SELECT q.*, u.full_name AS teacher_name, c.name AS cat_name, " +
                        "(SELECT COUNT(*) FROM questions WHERE quiz_id = q.id) AS qcount " +
                        "FROM quizzes q JOIN users u ON q.teacher_id=u.id " +
                        "JOIN categories c ON q.category_id=c.id WHERE q.category_id=? ORDER BY q.created_at DESC",
                categoryId);
    }

    public int insert(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, description, teacher_id, category_id, time_limit) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            ps.setInt(3, quiz.getTeacherId());
            ps.setInt(4, quiz.getCategoryId());
            ps.setInt(5, quiz.getTimeLimit());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) { int id = keys.getInt(1); quiz.setId(id); return id; }
        }
        return -1;
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM quizzes WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    private List<Quiz> queryQuizzes(String sql, Integer param) throws SQLException {
        if (param != null) {
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, param);
                return mapList(ps.executeQuery());
            }
        } else {
            try (Statement st = DBConnection.getConnection().createStatement()) {
                return mapList(st.executeQuery(sql));
            }
        }
    }

    private List<Quiz> mapList(ResultSet rs) throws SQLException {
        List<Quiz> list = new ArrayList<>();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    private Quiz map(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();
        q.setId(rs.getInt("id"));
        q.setTitle(rs.getString("title"));
        q.setDescription(rs.getString("description"));
        q.setTeacherId(rs.getInt("teacher_id"));
        q.setTeacherName(rs.getString("teacher_name"));
        q.setCategoryId(rs.getInt("category_id"));
        q.setCategoryName(rs.getString("cat_name"));
        q.setTimeLimit(rs.getInt("time_limit"));
        q.setQuestionCount(rs.getInt("qcount"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        return q;
    }
}