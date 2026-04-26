package com.miniProjectApp.presentation.panels;

import com.miniProjectApp.entity.Result;
import  com.miniProjectApp.presentation.theme.AppTheme;
import com.miniProjectApp.presentation.components.UIComponents;
import  com.miniProjectApp.metier.ElearningService;
import  com.miniProjectApp.entity.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
public class ResultPanel extends JPanel {

    public interface ResultAction {
        void onReturnToDashboard();
    }

    public ResultPanel(Result result, ResultAction action) {

        setBackground(AppTheme.BG_DARK);
        setLayout(new GridBagLayout());
        build(result, action);
    }

    private void build(Result result, ResultAction action) {
        UIComponents.CardPanel card = new UIComponents.CardPanel(20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));
        card.setPreferredSize(new Dimension(520, 500));

        // Grade circle
        JPanel gradeCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = gradeColor(result.getPercentage());
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 25));
                g2.fillOval(0,0,120,120);
                g2.setColor(c);
                g2.setStroke(new BasicStroke(5f));
                g2.drawOval(3,3,114,114);
                g2.setFont(new Font("Segoe UI",Font.BOLD,36));
                FontMetrics fm = g2.getFontMetrics();
                String pct = result.getPercentage() + "%";
                g2.drawString(pct, (120-fm.stringWidth(pct))/2, 64);
                g2.setFont(new Font("Segoe UI",Font.BOLD,16));
                fm = g2.getFontMetrics();
                String grade = result.getGrade();
                g2.drawString(grade, (120-fm.stringWidth(grade))/2, 86);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(120,120); }
        };
        gradeCircle.setOpaque(false);
        gradeCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel doneLabel = new JLabel("Quiz Complete!");
        doneLabel.setFont(AppTheme.FONT_TITLE);
        doneLabel.setForeground(AppTheme.TEXT_PRIMARY);
        doneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel quizName = new JLabel(result.getQuizTitle() != null ? result.getQuizTitle() : "");
        quizName.setFont(AppTheme.FONT_BODY);
        quizName.setForeground(AppTheme.TEXT_SECONDARY);
        quizName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stats grid
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.setMaximumSize(new Dimension(420, 80));
        stats.add(UIComponents.statCard("Correct",   result.getCorrectAnswers() + "/" + result.getTotalQuestions(), AppTheme.SUCCESS));
        stats.add(UIComponents.statCard("Score",     result.getPercentage() + "%", AppTheme.ACCENT));
        stats.add(UIComponents.statCard("Grade",     result.getGrade(), gradeColor(result.getPercentage())));

        // Motivational message
        JLabel msg = new JLabel(motivationalMsg(result.getPercentage()));
        msg.setFont(AppTheme.FONT_BODY);
        msg.setForeground(AppTheme.TEXT_SECONDARY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.ModernButton backBtn = new UIComponents.ModernButton("← Back to Dashboard");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(300, AppTheme.BTN_HEIGHT));
        backBtn.addActionListener(e -> action.onReturnToDashboard());

        card.add(gradeCircle);
        card.add(Box.createVerticalStrut(20));
        card.add(doneLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(quizName);
        card.add(Box.createVerticalStrut(24));
        card.add(stats);
        card.add(Box.createVerticalStrut(20));
        card.add(msg);
        card.add(Box.createVerticalStrut(30));
        card.add(backBtn);

        add(card);
    }

    private Color gradeColor(int pct) {
        if (pct >= 80) return AppTheme.SUCCESS;
        if (pct >= 60) return AppTheme.WARNING;
        return AppTheme.DANGER;
    }

    private String motivationalMsg(int pct) {
        if (pct >= 90) return "🏆 Outstanding! You nailed it!";
        if (pct >= 75) return "🌟 Great job! Keep it up!";
        if (pct >= 60) return "👍 Good work! A little more practice and you'll excel.";
        if (pct >= 50) return "📚 Not bad — review the material and try again.";
        return "💪 Don't give up! Study the topic and retry.";
    }
}