package com.miniProjectApp.presentation;

import  com.miniProjectApp.presentation.components.*;
import  com.miniProjectApp.presentation.theme.AppTheme;
import  com.miniProjectApp.metier.ElearningService;
import  com.miniProjectApp.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import com.miniProjectApp.presentation.panels.*;

/**
 * MainWindow – the top-level JFrame shell.
 *
 * Layout:
 *   ┌────────────┬──────────────────────────────────┐
 *   │  Sidebar   │       Content (CardLayout)        │
 *   │  (220px)   │   Dashboard / Quiz / Results …    │
 *   └────────────┴──────────────────────────────────┘
 *
 * CardLayout pages
 *  ● LOGIN
 *  ● STUDENT_DASH
 *  ● TEACHER_DASH
 *  ● QUIZ
 *  ● RESULT
 */
public class MainWindow extends JFrame {

    private final ElearningService service = new ElearningService();

    // Top-level card layout
    private final CardLayout rootCards   = new CardLayout();
    private final JPanel     rootPanel   = new JPanel(rootCards);

    // App shell (sidebar + content) shown after login
    private JPanel   appShell;
    private Sidebar  sidebar;
    private CardLayout contentCards;
    private JPanel   contentPanel;

    // Current user
    private User currentUser;

    // ── Constructor ───────────────────────────────────────────────────────
    public MainWindow() {
        super("EduLearn – E-Learning Platform");
        setupFrame();
        buildRootLayout();
        showLogin();
        setVisible(true);
    }

    // ── Frame setup ───────────────────────────────────────────────────────
    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        // Custom dark title bar feel on supported systems
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        applyGlobalUI();
    }

    private void applyGlobalUI() {
        // Override global L&F colours so Swing components inherit dark theme
        UIManager.put("Panel.background",          AppTheme.BG_DARK);
        UIManager.put("OptionPane.background",     AppTheme.BG_CARD);
        UIManager.put("OptionPane.messageForeground", AppTheme.TEXT_PRIMARY);
        UIManager.put("Button.background",         AppTheme.BG_HOVER);
        UIManager.put("Button.foreground",         AppTheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.background",       AppTheme.BG_INPUT);
        UIManager.put("ComboBox.foreground",       AppTheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", AppTheme.ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("Spinner.background",        AppTheme.BG_INPUT);
        UIManager.put("Spinner.foreground",        AppTheme.TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", AppTheme.ACCENT_LIGHT);
        UIManager.put("ScrollPane.background",     AppTheme.BG_DARK);
        UIManager.put("Viewport.background",       AppTheme.BG_DARK);
        UIManager.put("Dialog.background",         AppTheme.BG_CARD);
    }

    // ── Root layout: LOGIN page  ↔  APP SHELL ────────────────────────────
    private void buildRootLayout() {
        rootPanel.setBackground(AppTheme.BG_DARK);
        setContentPane(rootPanel);

        // Login page (added once)
        LoginPanel loginPanel = new LoginPanel(service, user -> {
            currentUser = user;
            buildAppShell();          // re-build shell for this user
            rootCards.show(rootPanel, "APP");
        });
        rootPanel.add(loginPanel, "LOGIN");
    }

    private void showLogin() {
        rootCards.show(rootPanel, "LOGIN");
    }

    // ── App Shell (built fresh on each login) ─────────────────────────────
    private void buildAppShell() {
        // Remove previous shell if any
        for (Component c : rootPanel.getComponents()) {
            if ("APP".equals(((JPanel)rootPanel).getClientProperty("APP"))) rootPanel.remove(c);
        }

        appShell = new JPanel(new BorderLayout());
        appShell.setBackground(AppTheme.BG_DARK);

        // Content area
        contentCards = new CardLayout();
        contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(AppTheme.BG_DARK);

        // Build sidebar based on role
        sidebar = new Sidebar(currentUser, this::handleNav);

        if (currentUser.getRole() == User.Role.TEACHER) {
            buildTeacherShell();
        } else {
            buildStudentShell();
        }

        appShell.add(sidebar,       BorderLayout.WEST);
        appShell.add(contentPanel,  BorderLayout.CENTER);

        rootPanel.add(appShell, "APP");
        rootCards.show(rootPanel, "APP");
    }

    // ── TEACHER shell ─────────────────────────────────────────────────────
    private void buildTeacherShell() {
        sidebar.addNavItem("🏠", "Dashboard",  "TEACHER_DASH");
        sidebar.addNavItem("📝", "My Quizzes", "TEACHER_DASH");
        sidebar.addNavItem("📊", "Statistics", "TEACHER_STATS");

        TeacherDashboard teacherDash = new TeacherDashboard(service, currentUser, this);
        contentPanel.add(teacherDash, "TEACHER_DASH");

        JPanel statsPanel = buildComingSoon("Statistics", "Coming soon – charts of your quiz performance!");
        contentPanel.add(statsPanel, "TEACHER_STATS");

        sidebar.setActiveItem("TEACHER_DASH");
        contentCards.show(contentPanel, "TEACHER_DASH");
    }

    // ── STUDENT shell ─────────────────────────────────────────────────────
    private void buildStudentShell() {
        sidebar.addNavItem("🏠",  "Dashboard",   "STUDENT_DASH");
        sidebar.addNavItem("🎯",  "Browse Quizzes","STUDENT_DASH");
        sidebar.addNavItem("📈",  "My Results",  "STUDENT_RESULTS");

        // Student quiz browser
        StudentDashboard studentDash = new StudentDashboard(
            service, currentUser, this,
            quiz -> launchQuiz(quiz)            // callback when user clicks Start
        );
        contentPanel.add(studentDash, "STUDENT_DASH");

        // Results history
        JPanel resultsPanel = buildResultsHistory();
        contentPanel.add(resultsPanel, "STUDENT_RESULTS");

        sidebar.setActiveItem("STUDENT_DASH");
        contentCards.show(contentPanel, "STUDENT_DASH");
    }

    // ── Navigation handler ────────────────────────────────────────────────
    private void handleNav(String destination) {
        switch (destination) {
            case "LOGOUT":
                doLogout();
                break;
            default:
                contentCards.show(contentPanel, destination);
                break;
        }
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?", "Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            currentUser = null;
            rootPanel.remove(appShell);
            // Re-build login
            buildRootLayout();
            showLogin();
        }
    }

    // ── Quiz flow ─────────────────────────────────────────────────────────
    private void launchQuiz(Quiz quiz) {
        try {
            List<Question> questions = service.getQuizQuestions(quiz.getId());
            if (questions.isEmpty()) {
                UIComponents.showToast(this, "This quiz has no questions yet.", false);
                return;
            }

            QuizPanel quizPanel = new QuizPanel(
                service, currentUser, quiz, questions, this,
                result -> showResult(result)       // callback on submit
            );

            // Replace any previous quiz panel
            contentPanel.add(quizPanel, "QUIZ");
            contentCards.show(contentPanel, "QUIZ");
            sidebar.setActiveItem("");             // no nav item active during quiz

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not load quiz: " + ex.getMessage());
        }
    }

    private void showResult(Result result) {
        ResultPanel resultPanel = new ResultPanel(result, () -> {
            // Return to student dashboard and refresh results
            refreshResultsHistory();
            contentCards.show(contentPanel, "STUDENT_DASH");
            sidebar.setActiveItem("STUDENT_DASH");
        });

        contentPanel.add(resultPanel, "RESULT");
        contentCards.show(contentPanel, "RESULT");
        sidebar.setActiveItem("");
    }

    // ── Results history panel ─────────────────────────────────────────────
    private JPanel buildResultsHistory() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppTheme.BG_DARK);
        wrapper.setName("RESULTS_WRAPPER");
        refreshResultsHistoryInto(wrapper);
        return wrapper;
    }

    private void refreshResultsHistory() {
        // Find the STUDENT_RESULTS panel and rebuild its contents
        for (Component c : contentPanel.getComponents()) {
            if (c instanceof JPanel && "RESULTS_WRAPPER".equals(((JPanel)c).getName())) {
                JPanel wrapper = (JPanel) c;
                wrapper.removeAll();
                refreshResultsHistoryInto(wrapper);
                wrapper.revalidate();
                wrapper.repaint();
                break;
            }
        }
    }

    private void refreshResultsHistoryInto(JPanel wrapper) {
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(AppTheme.BG_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_DARK);
        header.setBorder(new EmptyBorder(24, 32, 16, 32));
        JLabel title = new JLabel("My Results");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel sub = new JLabel("All your quiz attempts and scores");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);
        JPanel hl = new JPanel();
        hl.setLayout(new BoxLayout(hl, BoxLayout.Y_AXIS));
        hl.setOpaque(false);
        hl.add(title); hl.add(sub);
        header.add(hl, BorderLayout.WEST);

        // Table
        String[] cols = {"Quiz", "Score", "Correct", "Grade", "Time Taken", "Date"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        try {
            List<Result> results = service.getStudentResults(currentUser.getId());

            // Summary stats
            int totalAttempts = results.size();
            int avgScore = results.stream().mapToInt(Result::getPercentage).sum();
            if (totalAttempts > 0) avgScore /= totalAttempts;
            long passed = results.stream().filter(r -> r.getPercentage() >= 50).count();

            JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
            statsRow.setOpaque(false);
            statsRow.setBorder(new EmptyBorder(0, 32, 16, 32));
            statsRow.add(UIComponents.statCard("Attempts",   String.valueOf(totalAttempts), AppTheme.ACCENT));
            statsRow.add(UIComponents.statCard("Avg Score",  avgScore + "%",                AppTheme.PURPLE));
            statsRow.add(UIComponents.statCard("Passed",     String.valueOf(passed),         AppTheme.SUCCESS));

            for (Result r : results) {
                int mins = r.getTimeTaken() / 60;
                int secs = r.getTimeTaken() % 60;
                model.addRow(new Object[]{
                    r.getQuizTitle(),
                    r.getPercentage() + "%",
                    r.getCorrectAnswers() + " / " + r.getTotalQuestions(),
                    r.getGrade(),
                    String.format("%dm %ds", mins, secs),
                    r.getTakenAt() != null ? r.getTakenAt().toString().substring(0, 16) : "-"
                });
            }

            JPanel body = new JPanel(new BorderLayout(0, 0));
            body.setBackground(AppTheme.BG_DARK);
            body.setBorder(new EmptyBorder(0, 32, 24, 32));

            javax.swing.JTable table = UIComponents.createTable(cols);
            table.setModel(model);
            UIComponents.CardPanel tableCard = new UIComponents.CardPanel(12);
            tableCard.setLayout(new BorderLayout());
            tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));
            tableCard.add(UIComponents.sectionHeader("Quiz History", results.size() + " attempt(s)"), BorderLayout.NORTH);
            tableCard.add(UIComponents.wrapTable(table), BorderLayout.CENTER);

            body.add(statsRow, BorderLayout.NORTH);
            body.add(tableCard, BorderLayout.CENTER);

            wrapper.add(header, BorderLayout.NORTH);
            wrapper.add(body, BorderLayout.CENTER);

        } catch (Exception ex) {
            wrapper.add(header, BorderLayout.NORTH);
            JLabel err = new JLabel("Error loading results: " + ex.getMessage());
            err.setForeground(AppTheme.DANGER);
            err.setHorizontalAlignment(SwingConstants.CENTER);
            wrapper.add(err, BorderLayout.CENTER);
        }
    }

    // ── Placeholder panel ─────────────────────────────────────────────────
    private JPanel buildComingSoon(String title, String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppTheme.BG_DARK);

        UIComponents.CardPanel card = new UIComponents.CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel icon = new JLabel("🚧");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel(title);
        t.setFont(AppTheme.FONT_TITLE);
        t.setForeground(AppTheme.TEXT_PRIMARY);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel m = new JLabel(msg);
        m.setFont(AppTheme.FONT_BODY);
        m.setForeground(AppTheme.TEXT_SECONDARY);
        m.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(m);

        p.add(card);
        return p;
    }

    // ── Entry point ───────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
