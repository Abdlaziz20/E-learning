package com.miniProjectApp.presentation.panels;

import com.miniProjectApp.presentation.theme.AppTheme;
import com.miniProjectApp.presentation.components.UIComponents;
import com.miniProjectApp.metier.ElearningService;
import com.miniProjectApp.entity.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class QuizPanel extends JPanel {

    public interface QuizCompleteListener {
        void onComplete(Result result);
    }

    private final ElearningService service;
    private final User student;
    private final Quiz quiz;
    private final List<Question> questions;
    private final QuizCompleteListener listener;
    private final JFrame parentFrame;

    private String[] answers;
    private int currentIndex = 0;
    private long startTime;

    // UI
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private JLabel questionLabel;
    private JPanel optionsPanel;
    private ButtonGroup btnGroup;
    private JLabel timerLabel;
    private Timer countdownTimer;
    private int secondsLeft;

    public QuizPanel(ElearningService service, User student, Quiz quiz,
                     List<Question> questions, JFrame frame, QuizCompleteListener listener) {
        this.service   = service;
        this.student   = student;
        this.quiz      = quiz;
        this.questions = questions;
        this.listener  = listener;
        this.parentFrame = frame;
        this.answers   = new String[questions.size()];
        this.startTime = System.currentTimeMillis();
        this.secondsLeft = quiz.getTimeLimit() * 60;

        setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());
        build();
        showQuestion(0);
        startTimer();
    }

    private void build() {
        // Header with timer
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_DARK);
        header.setBorder(new EmptyBorder(20, 32, 12, 32));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel quizTitle = new JLabel(quiz.getTitle());
        quizTitle.setFont(AppTheme.FONT_HEADING);
        quizTitle.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel teacher = new JLabel("By " + quiz.getTeacherName() + "  ·  " + quiz.getCategoryName());
        teacher.setFont(AppTheme.FONT_SMALL);
        teacher.setForeground(AppTheme.TEXT_MUTED);

        left.add(quizTitle);
        left.add(teacher);

        // Timer
        timerLabel = new JLabel("⏱ " + formatTime(secondsLeft));
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timerLabel.setForeground(AppTheme.SUCCESS);

        header.add(left, BorderLayout.WEST);
        header.add(timerLabel, BorderLayout.EAST);

        // Progress
        JPanel progressRow = new JPanel(new BorderLayout(12, 0));
        progressRow.setBackground(AppTheme.BG_DARK);
        progressRow.setBorder(new EmptyBorder(0, 32, 0, 32));

        progressLabel = new JLabel("Question 1 of " + questions.size());
        progressLabel.setFont(AppTheme.FONT_BODY);
        progressLabel.setForeground(AppTheme.TEXT_SECONDARY);

        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(1);
        progressBar.setBackground(AppTheme.BG_CARD);
        progressBar.setForeground(AppTheme.ACCENT);
        progressBar.setBorder(null);
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setBorderPainted(false);

        progressRow.add(progressLabel, BorderLayout.WEST);
        progressRow.add(progressBar, BorderLayout.CENTER);

        JPanel topSection = new JPanel(new BorderLayout(0, 8));
        topSection.setBackground(AppTheme.BG_DARK);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(progressRow, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);

        // Question card
        UIComponents.CardPanel qCard = new UIComponents.CardPanel(16);
        qCard.setLayout(new BorderLayout(0, 24));
        qCard.setBorder(new EmptyBorder(32, 40, 32, 40));

        questionLabel = new JLabel("<html><body style='width:600px'>Question text</body></html>");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        questionLabel.setForeground(AppTheme.TEXT_PRIMARY);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);

        // Navigation buttons
        JPanel navButtons = new JPanel(new BorderLayout());
        navButtons.setOpaque(false);

        UIComponents.ModernButton prevBtn = UIComponents.ModernButton.secondary("← Previous");
        UIComponents.ModernButton nextBtn = new UIComponents.ModernButton("Next →");
        UIComponents.ModernButton submitBtn = UIComponents.ModernButton.success("Submit Quiz ✓");

        prevBtn.addActionListener(e -> { saveCurrentAnswer(); if (currentIndex > 0) showQuestion(currentIndex - 1); });
        nextBtn.addActionListener(e -> { saveCurrentAnswer(); if (currentIndex < questions.size()-1) showQuestion(currentIndex + 1); });
        submitBtn.addActionListener(e -> { saveCurrentAnswer(); submitQuiz(); });

        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftNav.setOpaque(false);
        leftNav.add(prevBtn);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightNav.setOpaque(false);
        rightNav.add(nextBtn);
        rightNav.add(submitBtn);

        navButtons.add(leftNav, BorderLayout.WEST);
        navButtons.add(rightNav, BorderLayout.EAST);

        qCard.add(questionLabel, BorderLayout.NORTH);
        qCard.add(optionsPanel, BorderLayout.CENTER);
        qCard.add(navButtons, BorderLayout.SOUTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(AppTheme.BG_DARK);
        body.setBorder(new EmptyBorder(16, 32, 24, 32));
        body.add(qCard, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    private void showQuestion(int index) {
        currentIndex = index;
        Question q = questions.get(index);

        progressLabel.setText("Question " + (index+1) + " of " + questions.size());
        progressBar.setValue(index + 1);
        questionLabel.setText("<html><body style='width:600px; font-size:13pt'>" +
                q.getQuestionText() + "</body></html>");

        // Rebuild options
        optionsPanel.removeAll();
        btnGroup = new ButtonGroup();
        String[] letters = {"A","B","C","D"};
        String[] texts   = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};

        for (int i = 0; i < 4; i++) {
            final String letter = letters[i];
            optionsPanel.add(buildOptionButton(letter, texts[i], letter.equals(answers[index])));
            optionsPanel.add(Box.createVerticalStrut(10));
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void repaintAllOptions() {
        for (int i = 0; i < optionsPanel.getComponentCount(); i++) {
            Component c = optionsPanel.getComponent(i);
            if (c instanceof JPanel) {
                c.repaint();
                for (Component child : ((JPanel) c).getComponents()) {
                    child.repaint();
                }
            }
        }
    }

    private JPanel buildOptionButton(String letter, String text, boolean selected) {
        JPanel outer = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = ((JRadioButton)getComponent(0)).isSelected();
                if (sel) {
                    g2.setColor(new Color(AppTheme.ACCENT.getRed(), AppTheme.ACCENT.getGreen(), AppTheme.ACCENT.getBlue(), 20));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    g2.setColor(AppTheme.ACCENT);
                } else {

                    g2.setColor(AppTheme.BG_CARD);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    g2.setColor(AppTheme.BORDER);
                }
                g2.setStroke(new BasicStroke(sel ? 2f : 1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        outer.setOpaque(false);
        outer.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        outer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        outer.setMaximumSize(new Dimension(700, 56));

        // Letter badge
        JLabel badge = new JLabel(letter) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                JRadioButton rb = (JRadioButton)outer.getComponent(0);
                g2.setColor(rb.isSelected() ? AppTheme.ACCENT : AppTheme.BORDER);
                g2.fillRoundRect(0,0,28,28,8,8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI",Font.BOLD,12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter, (28-fm.stringWidth(letter))/2, 19);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(28,28); }
        };
        badge.setOpaque(false);

        JRadioButton rb = new JRadioButton(text, selected);
        rb.setFont(AppTheme.FONT_BODY);
        rb.setForeground(AppTheme.TEXT_PRIMARY);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        // ╔══════════════════════════════════════════════╗
        // ║  FIX — remplacer outer.repaint()             ║
        // ║  par repaintAllOptions()                     ║
        // ╚══════════════════════════════════════════════╝
        rb.addActionListener(e -> repaintAllOptions());

        btnGroup.add(rb);

        outer.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                rb.setSelected(true);
                // ╔══════════════════════════════════════════════╗
                // ║  FIX — remplacer outer.repaint()             ║
                // ║  par repaintAllOptions()                     ║
                // ╚══════════════════════════════════════════════╝
                repaintAllOptions();
            }
        });

        outer.add(rb);
        outer.add(badge);
        return outer;
    }

    private void saveCurrentAnswer() {
        if (btnGroup == null) return;
        String[] letters = {"A","B","C","D"};
        for (int i = 0; i < 4; i++) {
            Component c = optionsPanel.getComponent(i * 2);
            if (c instanceof JPanel) {
                JPanel op = (JPanel)c;
                if (op.getComponent(0) instanceof JRadioButton) {
                    JRadioButton rb = (JRadioButton)op.getComponent(0);
                    if (rb.isSelected()) { answers[currentIndex] = letters[i]; return; }
                }
            }
        }
        answers[currentIndex] = null;
    }

    private void submitQuiz() {
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Submit quiz now? You cannot change your answers after.",
                "Confirm Submit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (countdownTimer != null) countdownTimer.stop();
        int timeTaken = (int)((System.currentTimeMillis() - startTime) / 1000);
        try {
            Result result = service.gradeQuiz(student.getId(), quiz, questions, answers, timeTaken);
            if (listener != null) listener.onComplete(result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "Error submitting: " + ex.getMessage());
        }
    }

    private void startTimer() {
        countdownTimer = new Timer(1000, e -> {
            secondsLeft--;
            timerLabel.setText("⏱ " + formatTime(secondsLeft));
            if (secondsLeft <= 60) timerLabel.setForeground(AppTheme.WARNING);
            if (secondsLeft <= 0) { ((Timer)e.getSource()).stop(); submitQuiz(); }
        });
        countdownTimer.start();
    }

    private String formatTime(int secs) {
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }
}