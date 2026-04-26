package com.miniProjectApp.presentation.panels;

import com.miniProjectApp.presentation.theme.AppTheme;
import com.miniProjectApp.presentation.components.UIComponents;
import com.miniProjectApp.metier.ElearningService;
import com.miniProjectApp.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Teacher Dashboard  – manage quizzes, add questions step-by-step
 */
public class TeacherDashboard extends JPanel {

    private final ElearningService service;
    private final User teacher;
    private final JFrame parentFrame;

    // Quiz creation state
    private Quiz currentQuiz = null;

    // Tables
    private JTable quizTable;
    private JTable questionTable;
    private DefaultTableModel quizModel;
    private DefaultTableModel questionModel;

    // Form fields
    private UIComponents.ModernField    quizTitle;
    private JTextArea                   quizDesc;
    private JComboBox<Object>           quizCategory;
    private JSpinner                    quizTime;
    private UIComponents.ModernField    qText;
    private UIComponents.ModernField    optA, optB, optC, optD;
    private JComboBox<String>           correctOpt;

    // Labels
    private JLabel quizStatusLabel;
    private JLabel questionCountLabel;

    private CardLayout mainCards;
    private JPanel mainCardPanel;

    public TeacherDashboard(ElearningService service, User teacher, JFrame frame) {
        this.service     = service;
        this.teacher     = teacher;
        this.parentFrame = frame;
        setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());
        build();
        loadQuizzes();
    }

    private void build() {
        // Header
        add(buildHeader(), BorderLayout.NORTH);

        // Content with CardLayout: Dashboard view / Quiz-create wizard
        mainCards     = new CardLayout();
        mainCardPanel = new JPanel(mainCards);
        mainCardPanel.setBackground(AppTheme.BG_DARK);
        mainCardPanel.add(buildDashboardView(), "DASHBOARD");
        mainCardPanel.add(buildQuizWizard(),    "WIZARD");
        add(mainCardPanel, BorderLayout.CENTER);
    }

    // ── HEADER ──────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(AppTheme.BG_DARK);
        h.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Teacher Dashboard");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Welcome back, " + teacher.getFullName());
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        left.add(title);
        left.add(sub);

        UIComponents.ModernButton createBtn = new UIComponents.ModernButton("＋  New Quiz");
        createBtn.addActionListener(e -> showWizard());

        h.add(left, BorderLayout.WEST);
        h.add(createBtn, BorderLayout.EAST);
        return h;
    }

    // ── DASHBOARD VIEW ───────────────────────────────────────────────────
    private JPanel buildDashboardView() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(0, 32, 24, 32));

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        // (stats populated on loadQuizzes)
        statsRow.setName("statsRow");
        p.add(statsRow, BorderLayout.NORTH);

        // Quiz table
        String[] cols = {"Quiz Title", "Category", "Questions", "Time Limit", "Created"};
        quizModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        quizTable = UIComponents.createTable(cols);
        quizTable.setModel(quizModel);

        JScrollPane scroll = UIComponents.wrapTable(quizTable);

        // Action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        actions.setOpaque(false);

        UIComponents.ModernButton viewQBtn = new UIComponents.ModernButton("📋  View Questions",
            new Color(0x6366F1), new Color(0x4F46E5));
        UIComponents.ModernButton addQBtn  = new UIComponents.ModernButton("＋  Add Question",
            AppTheme.SUCCESS, new Color(0x059669));
        UIComponents.ModernButton delBtn   = UIComponents.ModernButton.danger("🗑  Delete Quiz");

        viewQBtn.addActionListener(e -> viewQuestionsOfSelected());
        addQBtn.addActionListener(e  -> addQuestionToSelected());
        delBtn.addActionListener(e   -> deleteSelectedQuiz());

        actions.add(viewQBtn);
        actions.add(addQBtn);
        actions.add(delBtn);

        JPanel tableCard = new UIComponents.CardPanel(12);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(UIComponents.sectionHeader("My Quizzes", "Click a quiz to select, then use actions below"), BorderLayout.NORTH);
        ((JPanel)tableCard).setBorder(new EmptyBorder(16, 16, 0, 16));
        tableCard.add(scroll, BorderLayout.CENTER);
        tableCard.add(actions, BorderLayout.SOUTH);

        p.add(tableCard, BorderLayout.CENTER);
        return p;
    }

    // ── QUIZ CREATION WIZARD ─────────────────────────────────────────────
    private JPanel buildQuizWizard() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(0, 32, 24, 32));

        // LEFT – quiz info form
        p.add(buildQuizInfoPanel(), BorderLayout.WEST);
        // RIGHT – question form + list
        p.add(buildQuestionPanel(), BorderLayout.CENTER);

        return p;
    }

    private JPanel buildQuizInfoPanel() {
        UIComponents.CardPanel card = new UIComponents.CardPanel(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(320, 0));

        JLabel heading = new JLabel("Step 1 – Quiz Info");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        quizStatusLabel = new JLabel(" ");
        quizStatusLabel.setFont(AppTheme.FONT_SMALL);
        quizStatusLabel.setForeground(AppTheme.SUCCESS);
        quizStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        quizTitle = new UIComponents.ModernField("Quiz title", 18);
        quizTitle.setMaximumSize(new Dimension(999, AppTheme.INPUT_HEIGHT));
        quizTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        quizDesc = UIComponents.modernTextArea(4, 18);
        JScrollPane descScroll = UIComponents.wrapTextArea(quizDesc);
        descScroll.setMaximumSize(new Dimension(999, 90));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Category combo – load from DB
        quizCategory = new JComboBox<>();
        quizCategory.setBackground(AppTheme.BG_INPUT);
        quizCategory.setForeground(AppTheme.TEXT_PRIMARY);
        quizCategory.setFont(AppTheme.FONT_BODY);
        quizCategory.setMaximumSize(new Dimension(999, AppTheme.INPUT_HEIGHT));
        quizCategory.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadCategories();

        quizTime = new JSpinner(new SpinnerNumberModel(20, 5, 120, 5));
        quizTime.setFont(AppTheme.FONT_BODY);
        ((JSpinner.NumberEditor)quizTime.getEditor()).getTextField().setBackground(AppTheme.BG_INPUT);
        ((JSpinner.NumberEditor)quizTime.getEditor()).getTextField().setForeground(AppTheme.TEXT_PRIMARY);
        quizTime.setMaximumSize(new Dimension(999, AppTheme.INPUT_HEIGHT));
        quizTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        UIComponents.ModernButton saveQuizBtn = new UIComponents.ModernButton("Create Quiz →");
        saveQuizBtn.setMaximumSize(new Dimension(999, AppTheme.BTN_HEIGHT));
        saveQuizBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveQuizBtn.addActionListener(e -> createQuiz());

        UIComponents.ModernButton backBtn = UIComponents.ModernButton.secondary("← Back");
        backBtn.setMaximumSize(new Dimension(999, AppTheme.BTN_HEIGHT));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> { mainCards.show(mainCardPanel, "DASHBOARD"); loadQuizzes(); });

        card.add(heading);
        card.add(Box.createVerticalStrut(4));
        card.add(quizStatusLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(label("Quiz title *")); card.add(Box.createVerticalStrut(4));
        card.add(quizTitle);
        card.add(Box.createVerticalStrut(12));
        card.add(label("Description")); card.add(Box.createVerticalStrut(4));
        card.add(descScroll);
        card.add(Box.createVerticalStrut(12));
        card.add(label("Category *")); card.add(Box.createVerticalStrut(4));
        card.add(quizCategory);
        card.add(Box.createVerticalStrut(12));
        card.add(label("Time limit (minutes)")); card.add(Box.createVerticalStrut(4));
        card.add(quizTime);
        card.add(Box.createVerticalStrut(20));
        card.add(saveQuizBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(backBtn);

        return card;
    }

    private JPanel buildQuestionPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        // Question form (top)
        UIComponents.CardPanel formCard = new UIComponents.CardPanel(12);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Step 2 – Add Questions (one by one)");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        questionCountLabel = new JLabel("No questions added yet");
        questionCountLabel.setFont(AppTheme.FONT_SMALL);
        questionCountLabel.setForeground(AppTheme.TEXT_SECONDARY);
        questionCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        qText = new UIComponents.ModernField("Enter your question here...", 25);
        qText.setMaximumSize(new Dimension(999, AppTheme.INPUT_HEIGHT));
        qText.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Options – 2x2 grid
        optA = new UIComponents.ModernField("Option A", 12);
        optB = new UIComponents.ModernField("Option B", 12);
        optC = new UIComponents.ModernField("Option C", 12);
        optD = new UIComponents.ModernField("Option D", 12);

        JPanel optGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        optGrid.setOpaque(false);
        optGrid.setMaximumSize(new Dimension(999, 100));
        optGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel aRow = optRow("A", optA);
        JPanel bRow = optRow("B", optB);
        JPanel cRow = optRow("C", optC);
        JPanel dRow = optRow("D", optD);
        optGrid.add(aRow); optGrid.add(bRow);
        optGrid.add(cRow); optGrid.add(dRow);

        correctOpt = UIComponents.modernCombo(new String[]{"A","B","C","D"});
        correctOpt.setMaximumSize(new Dimension(150, AppTheme.INPUT_HEIGHT));
        correctOpt.setAlignmentX(Component.LEFT_ALIGNMENT);

        UIComponents.ModernButton addQBtn = UIComponents.ModernButton.success("＋  Add This Question");
        addQBtn.setMaximumSize(new Dimension(999, AppTheme.BTN_HEIGHT));
        addQBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addQBtn.addActionListener(e -> addQuestion());

        formCard.add(heading);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(questionCountLabel);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(label("Question text *"));
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(qText);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(label("Answer options *"));
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(optGrid);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(label("Correct answer"));
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(correctOpt);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(addQBtn);

        // Question list (bottom)
        String[] qCols = {"#", "Question", "Correct Answer"};
        questionModel = new DefaultTableModel(qCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        questionTable = UIComponents.createTable(qCols);
        questionTable.setModel(questionModel);

        UIComponents.CardPanel listCard = new UIComponents.CardPanel(12);
        listCard.setLayout(new BorderLayout());
        listCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        listCard.add(UIComponents.sectionHeader("Questions Added", ""), BorderLayout.NORTH);
        listCard.add(UIComponents.wrapTable(questionTable), BorderLayout.CENTER);

        p.add(formCard, BorderLayout.NORTH);
        p.add(listCard, BorderLayout.CENTER);
        return p;
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private JPanel optRow(String letter, UIComponents.ModernField field) {
        JPanel r = new JPanel(new BorderLayout(6, 0));
        r.setOpaque(false);
        JLabel lbl = new JLabel(letter);
        lbl.setFont(AppTheme.FONT_LABEL);
        lbl.setForeground(AppTheme.ACCENT);
        lbl.setPreferredSize(new Dimension(16, AppTheme.INPUT_HEIGHT));
        r.add(lbl, BorderLayout.WEST);
        r.add(field, BorderLayout.CENTER);
        return r;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(AppTheme.FONT_LABEL);
        l.setForeground(AppTheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── Actions ───────────────────────────────────────────────────────────
    private void showWizard() {
        currentQuiz = null;
        quizTitle.setText("");
        quizDesc.setText("");
        quizTime.setValue(20);
        questionModel.setRowCount(0);
        quizStatusLabel.setText(" ");
        questionCountLabel.setText("Create a quiz first, then add questions.");
        mainCards.show(mainCardPanel, "WIZARD");
    }

    private void loadCategories() {
        try {
            List<Category> cats = service.getAllCategories();
            quizCategory.removeAllItems();
            quizCategory.addItem("-- Select Category --");
            for (Category c : cats) quizCategory.addItem(c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
        }
    }

    private void createQuiz() {
        if (currentQuiz != null) {
            quizStatusLabel.setText("✓  Quiz already created. Add questions below.");
            return;
        }
        String title = quizTitle.getText().trim();
        String desc  = quizDesc.getText().trim();
        int time     = (Integer) quizTime.getValue();
        Object selCat = quizCategory.getSelectedItem();

        if (!(selCat instanceof Category)) {
            UIComponents.showToast(parentFrame, "Please select a category", false);
            return;
        }

        try {
            currentQuiz = service.createQuiz(title, desc, teacher.getId(),
                ((Category) selCat).getId(), time);
            quizStatusLabel.setText("✓  Quiz \"" + currentQuiz.getTitle() + "\" created! Now add questions.");
            questionCountLabel.setText("0 questions — keep adding!");
            UIComponents.showToast(parentFrame, "Quiz created! Now add questions.", true);
            quizTitle.setEnabled(false);
            quizDesc.setEnabled(false);
            quizCategory.setEnabled(false);
            quizTime.setEnabled(false);
        } catch (Exception ex) {
            UIComponents.showToast(parentFrame, ex.getMessage(), false);
        }
    }

    private void addQuestion() {
        if (currentQuiz == null) {
            UIComponents.showToast(parentFrame, "Create the quiz first (Step 1)", false);
            return;
        }
        String qTxt = qText.getText().trim();
        String a    = optA.getText().trim();
        String b    = optB.getText().trim();
        String c    = optC.getText().trim();
        String d    = optD.getText().trim();
        String cor  = (String) correctOpt.getSelectedItem();

        try {
            service.addQuestion(currentQuiz.getId(), qTxt, a, b, c, d, cor, 1);
            int num = questionModel.getRowCount() + 1;
            questionModel.addRow(new Object[]{num, qTxt, cor + ": " + getOptText(cor, a, b, c, d)});
            questionCountLabel.setText(num + " question(s) added ✓");
            // Clear fields
            qText.setText(""); optA.setText(""); optB.setText(""); optC.setText(""); optD.setText("");
            UIComponents.showToast(parentFrame, "Question " + num + " added!", true);
        } catch (Exception ex) {
            UIComponents.showToast(parentFrame, ex.getMessage(), false);
        }
    }

    private String getOptText(String letter, String a, String b, String c, String d) {
        switch(letter) { case "A": return a; case "B": return b; case "C": return c; default: return d; }
    }

    private void loadQuizzes() {
        try {
            List<Quiz> quizzes = service.getTeacherQuizzes(teacher.getId());
            quizModel.setRowCount(0);
            for (Quiz q : quizzes) {
                quizModel.addRow(new Object[]{
                    q.getTitle(), q.getCategoryName(),
                    q.getQuestionCount() + " Qs",
                    q.getTimeLimit() + " min",
                    q.getCreatedAt() != null ? q.getCreatedAt().toString().substring(0,10) : "-"
                });
            }
            // Update stats
            updateStats(quizzes);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading quizzes: " + ex.getMessage());
        }
    }

    private void updateStats(List<Quiz> quizzes) {
        Component content = mainCardPanel.getComponent(0); // DASHBOARD card
        if (!(content instanceof JPanel)) return;
        for (Component c : ((JPanel)content).getComponents()) {
            if (c instanceof JPanel && "statsRow".equals(((JPanel)c).getName())) {
                JPanel sr = (JPanel)c;
                sr.removeAll();
                int totalQ = quizzes.stream().mapToInt(Quiz::getQuestionCount).sum();
                sr.add(UIComponents.statCard("Total Quizzes",   String.valueOf(quizzes.size()), AppTheme.ACCENT));
                sr.add(UIComponents.statCard("Total Questions", String.valueOf(totalQ),          AppTheme.PURPLE));
                sr.add(UIComponents.statCard("Categories",      "8",                             AppTheme.SUCCESS));
                sr.revalidate(); sr.repaint();
                break;
            }
        }
    }

    private void viewQuestionsOfSelected() {
        int row = quizTable.getSelectedRow();
        if (row < 0) { UIComponents.showToast(parentFrame, "Select a quiz first", false); return; }
        try {
            List<Quiz> quizzes = service.getTeacherQuizzes(teacher.getId());
            Quiz selected = quizzes.get(row);
            List<Question> questions = service.getQuizQuestions(selected.getId());

            JDialog dlg = new JDialog(parentFrame, "Questions – " + selected.getTitle(), true);
            dlg.setSize(700, 500);
            dlg.setLocationRelativeTo(parentFrame);

            JPanel panel = new JPanel(new BorderLayout(0, 0));
            panel.setBackground(AppTheme.BG_DARK);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            String[] cols = {"#","Question","A","B","C","D","Correct"};
            DefaultTableModel m = new DefaultTableModel(cols, 0);
            for (int i=0; i<questions.size(); i++) {
                Question q = questions.get(i);
                m.addRow(new Object[]{i+1, q.getQuestionText(),
                    q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                    q.getCorrectOption()});
            }
            JTable t = UIComponents.createTable(cols);
            t.setModel(m);
            panel.add(UIComponents.wrapTable(t), BorderLayout.CENTER);
            dlg.add(panel);
            dlg.setVisible(true);
        } catch (Exception ex) {
            UIComponents.showToast(parentFrame, ex.getMessage(), false);
        }
    }

    private void addQuestionToSelected() {
        int row = quizTable.getSelectedRow();
        if (row < 0) { UIComponents.showToast(parentFrame, "Select a quiz first", false); return; }
        try {
            List<Quiz> quizzes = service.getTeacherQuizzes(teacher.getId());
            currentQuiz = quizzes.get(row);
            questionModel.setRowCount(0);
            List<Question> qs = service.getQuizQuestions(currentQuiz.getId());
            for (int i=0; i<qs.size(); i++) {
                Question q = qs.get(i);
                questionModel.addRow(new Object[]{i+1, q.getQuestionText(), q.getCorrectOption()});
            }
            quizTitle.setText(currentQuiz.getTitle());
            quizTitle.setEnabled(false);
            quizStatusLabel.setText("✓  Adding to: " + currentQuiz.getTitle());
            questionCountLabel.setText(qs.size() + " existing question(s). Add more below.");
            mainCards.show(mainCardPanel, "WIZARD");
        } catch (Exception ex) {
            UIComponents.showToast(parentFrame, ex.getMessage(), false);
        }
    }

    private void deleteSelectedQuiz() {
        int row = quizTable.getSelectedRow();
        if (row < 0) { UIComponents.showToast(parentFrame, "Select a quiz first", false); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this quiz and all its questions?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            List<Quiz> quizzes = service.getTeacherQuizzes(teacher.getId());
            service.deleteQuiz(quizzes.get(row).getId());
            loadQuizzes();
            UIComponents.showToast(parentFrame, "Quiz deleted.", true);
        } catch (Exception ex) {
            UIComponents.showToast(parentFrame, ex.getMessage(), false);
        }
    }
}
