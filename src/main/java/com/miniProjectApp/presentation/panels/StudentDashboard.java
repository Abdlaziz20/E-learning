package com.miniProjectApp.presentation.panels;

import com.miniProjectApp.entity.Quiz;
import com.miniProjectApp.entity.User;
import com.miniProjectApp.presentation.components.UIComponents;
import com.miniProjectApp.entity.*;

import com.miniProjectApp.presentation.theme.AppTheme;
import com.miniProjectApp.presentation.components.UIComponents;
import com.miniProjectApp.metier.ElearningService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
public class StudentDashboard extends JPanel {

    public interface QuizStartListener {
        void onStartQuiz(Quiz quiz);
    }

    private final ElearningService service;
    private final User student;
    private final JFrame parentFrame;
    private final QuizStartListener startListener;

    private JComboBox<Object> teacherCombo;
    private JComboBox<Object> categoryCombo;
    private JTable quizTable;
    private DefaultTableModel quizModel;
    private List<Quiz> currentQuizList;

    public StudentDashboard(ElearningService service, User student,
                            JFrame frame, QuizStartListener startListener) {
        this.service       = service;
        this.student       = student;
        this.parentFrame   = frame;
        this.startListener = startListener;
        setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());
        build();
        loadData();
    }

    private void build() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setBackground(AppTheme.BG_DARK);
        body.setBorder(new EmptyBorder(0, 32, 24, 32));

        body.add(buildFilterPanel(), BorderLayout.WEST);
        body.add(buildQuizListPanel(), BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(AppTheme.BG_DARK);
        h.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Find a Quiz");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Hi " + student.getFullName() + " — choose your topic and start learning!");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        left.add(title);
        left.add(sub);
        h.add(left, BorderLayout.WEST);
        return h;
    }

    private JPanel buildFilterPanel() {
        UIComponents.CardPanel card = new UIComponents.CardPanel(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 18, 20, 18));
        card.setPreferredSize(new Dimension(220, 0));

        JLabel heading = new JLabel("🔍  Filter Quizzes");
        heading.setFont(AppTheme.FONT_SUBHEAD);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        teacherCombo  = new JComboBox<>();
        teacherCombo.setBackground(AppTheme.BG_INPUT);
        teacherCombo.setForeground(AppTheme.TEXT_PRIMARY);
        teacherCombo.setFont(AppTheme.FONT_BODY);
        teacherCombo.setMaximumSize(new Dimension(999, AppTheme.INPUT_HEIGHT));
        teacherCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        categoryCombo = new JComboBox<>();
        categoryCombo.setBackground(AppTheme.BG_INPUT);
        categoryCombo.setForeground(AppTheme.TEXT_PRIMARY);
        categoryCombo.setFont(AppTheme.FONT_BODY);
        categoryCombo.setMaximumSize(new Dimension(999, AppTheme.INPUT_HEIGHT));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        UIComponents.ModernButton searchBtn = new UIComponents.ModernButton("Apply Filter");
        searchBtn.setMaximumSize(new Dimension(999, AppTheme.BTN_HEIGHT));
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBtn.addActionListener(e -> applyFilter());

        UIComponents.ModernButton clearBtn = UIComponents.ModernButton.secondary("Clear");
        clearBtn.setMaximumSize(new Dimension(999, AppTheme.BTN_HEIGHT));
        clearBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearBtn.addActionListener(e -> { teacherCombo.setSelectedIndex(0); categoryCombo.setSelectedIndex(0); applyFilter(); });

        card.add(heading);
        card.add(Box.createVerticalStrut(20));
        card.add(fl("Teacher")); card.add(Box.createVerticalStrut(4));
        card.add(teacherCombo);
        card.add(Box.createVerticalStrut(14));
        card.add(fl("Category")); card.add(Box.createVerticalStrut(4));
        card.add(categoryCombo);
        card.add(Box.createVerticalStrut(20));
        card.add(searchBtn);
        card.add(Box.createVerticalStrut(6));
        card.add(clearBtn);

        return card;
    }

    private JPanel buildQuizListPanel() {
        String[] cols = {"Quiz Title", "Teacher", "Category", "Questions", "Time"};
        quizModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        quizTable = UIComponents.createTable(cols);
        quizTable.setModel(quizModel);

        // Double-click to start quiz
        quizTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) startSelectedQuiz();
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        actions.setOpaque(false);
        UIComponents.ModernButton startBtn = new UIComponents.ModernButton("▶  Start Quiz");
        startBtn.addActionListener(e -> startSelectedQuiz());
        JLabel hint = new JLabel("Or double-click a quiz to start");
        hint.setFont(AppTheme.FONT_SMALL);
        hint.setForeground(AppTheme.TEXT_MUTED);
        actions.add(startBtn);
        actions.add(hint);

        UIComponents.CardPanel card = new UIComponents.CardPanel(12);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 16, 0, 16));
        card.add(UIComponents.sectionHeader("Available Quizzes",
                "Select a quiz and click Start, or double-click"), BorderLayout.NORTH);
        card.add(UIComponents.wrapTable(quizTable), BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    private JLabel fl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(AppTheme.FONT_LABEL);
        l.setForeground(AppTheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void loadData() {
        try {
            // Teachers
            teacherCombo.removeAllItems();
            teacherCombo.addItem("All Teachers");
            for (User t : service.getAllTeachers()) teacherCombo.addItem(t);

            // Categories
            categoryCombo.removeAllItems();
            categoryCombo.addItem("All Categories");
            for (Category c : service.getAllCategories()) categoryCombo.addItem(c);

            applyFilter();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    private void applyFilter() {
        int teacherId   = 0;
        int categoryId  = 0;
        Object selT = teacherCombo.getSelectedItem();
        Object selC = categoryCombo.getSelectedItem();
        if (selT instanceof User)     teacherId  = ((User)selT).getId();
        if (selC instanceof Category) categoryId = ((Category)selC).getId();
        try {
            currentQuizList = service.filterQuizzes(teacherId, categoryId);
            quizModel.setRowCount(0);
            for (Quiz q : currentQuizList) {
                quizModel.addRow(new Object[]{
                        q.getTitle(), q.getTeacherName(),
                        q.getCategoryName(), q.getQuestionCount() + " Qs",
                        q.getTimeLimit() + " min"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void startSelectedQuiz() {
        int row = quizTable.getSelectedRow();
        if (row < 0) { UIComponents.showToast(parentFrame, "Select a quiz first", false); return; }
        if (currentQuizList == null || row >= currentQuizList.size()) return;
        Quiz quiz = currentQuizList.get(row);
        if (quiz.getQuestionCount() == 0) {
            UIComponents.showToast(parentFrame, "This quiz has no questions yet", false);
            return;
        }
        if (startListener != null) startListener.onStartQuiz(quiz);
    }
}