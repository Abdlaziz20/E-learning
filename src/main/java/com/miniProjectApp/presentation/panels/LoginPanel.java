package com.miniProjectApp.presentation.panels;

import com.miniProjectApp.presentation.theme.AppTheme;
import com.miniProjectApp.presentation.components.UIComponents;
import com.miniProjectApp.metier.ElearningService;
import com.miniProjectApp.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Login / Register Screen  (dark, modern design)
 */
public class LoginPanel extends JPanel {

    public interface AuthListener {
        void onLoginSuccess(User user);
    }

    private final ElearningService service;
    private final AuthListener listener;

    // State
    private boolean showingLogin = true;

    // Login fields
    private UIComponents.ModernField     loginEmail;
    private UIComponents.ModernPasswordField loginPass;

    // Register fields
    private UIComponents.ModernField     regName;
    private UIComponents.ModernField     regEmail;
    private UIComponents.ModernPasswordField regPass;
    private JComboBox<String>            regRole;

    private JPanel cardContainer;
    private CardLayout cardLayout;
    private JLabel errorLabel;

    public LoginPanel(ElearningService service, AuthListener listener) {
        this.service  = service;
        this.listener = listener;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_DARK);
        build();
    }

    private void build() {
        // ── LEFT panel (branding) ──────────────────────────────────────
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(0,0, new Color(0x1E40AF),
                        0, getHeight(), new Color(0x0F172A));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(255,255,255, 12));
                g2.fillOval(-60, -60, 280, 280);
                g2.fillOval(getWidth()-120, getHeight()-120, 240, 240);
                g2.setColor(new Color(255,255,255, 6));
                g2.fillOval(40, getHeight()/2, 200, 200);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        left.setOpaque(false);
        left.setLayout(new GridBagLayout());
        left.setPreferredSize(new Dimension(440, 0));

        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);

        // Big E logo
        JPanel logo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,30));
                g2.fillRoundRect(0,0,72,72,18,18);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("E", (72 - fm.stringWidth("E"))/2, 50);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(72,72); }
        };
        logo.setOpaque(false);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("EduLearn");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 38));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("The modern E-Learning platform.");
        tagline.setFont(AppTheme.FONT_BODY);
        tagline.setForeground(new Color(255,255,255,180));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(20));
        brand.add(appName);
        brand.add(Box.createVerticalStrut(8));
        brand.add(tagline);
        brand.add(Box.createVerticalStrut(40));

        // Features list
        String[][] features = {
            {"🎓", "Interactive quizzes"},
            {"📊", "Track your progress"},
            {"🏆", "Earn certifications"},
            {"👨‍🏫", "Expert teachers"}
        };
        for (String[] feat : features) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel icon = new JLabel(feat[0]);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            JLabel text = new JLabel(feat[1]);
            text.setFont(AppTheme.FONT_BODY);
            text.setForeground(new Color(255,255,255,200));
            row.add(icon);
            row.add(text);
            brand.add(row);
        }

        left.add(brand);

        // ── RIGHT panel (form) ─────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(AppTheme.BG_DARK);

        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBackground(AppTheme.BG_DARK);
        formWrapper.setOpaque(true);
        formWrapper.setPreferredSize(new Dimension(360, 520));

        // Tab switcher
        JPanel tabs = buildTabSwitcher();
        tabs.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card layout for Login/Register forms
        cardLayout   = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(AppTheme.BG_DARK);
        cardContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardContainer.add(buildLoginForm(),    "LOGIN");
        cardContainer.add(buildRegisterForm(), "REGISTER");

        formWrapper.add(Box.createVerticalGlue());
        formWrapper.add(tabs);
        formWrapper.add(Box.createVerticalStrut(24));
        formWrapper.add(errorLabel);
        formWrapper.add(Box.createVerticalStrut(8));
        formWrapper.add(cardContainer);
        formWrapper.add(Box.createVerticalGlue());

        right.add(formWrapper);

        // Assemble
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    // ── Tab switcher ────────────────────────────────────────────────────
    private JPanel buildTabSwitcher() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(AppTheme.BG_DARK);

        JPanel loginTab  = buildTab("Sign In",  true);
        JPanel registerTab = buildTab("Register", false);

        loginTab.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                showingLogin = true;
                cardLayout.show(cardContainer, "LOGIN");
                errorLabel.setText(" ");
                refreshTabs(loginTab, registerTab);
            }
        });
        registerTab.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                showingLogin = false;
                cardLayout.show(cardContainer, "REGISTER");
                errorLabel.setText(" ");
                refreshTabs(registerTab, loginTab);
            }
        });

        p.add(loginTab);
        p.add(Box.createHorizontalStrut(4));
        p.add(registerTab);
        return p;
    }

    private JPanel buildTab(String text, boolean active) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getClientProperty("active") == Boolean.TRUE) {
                    g2.setColor(AppTheme.ACCENT);
                    g2.fillRoundRect(0, getHeight()-3, getWidth(), 3, 2, 2);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setBackground(AppTheme.BG_DARK);
        p.putClientProperty("active", active);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel lbl = new JLabel(text);
        lbl.setFont(active ? new Font("Segoe UI", Font.BOLD, 18) : AppTheme.FONT_BODY);
        lbl.setForeground(active ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_MUTED);
        lbl.setName("tabLabel");

        p.add(lbl);
        p.setPreferredSize(new Dimension(110, 36));
        return p;
    }

    private void refreshTabs(JPanel active, JPanel inactive) {
        updateTab(active, true);
        updateTab(inactive, false);
    }

    private void updateTab(JPanel tab, boolean active) {
        tab.putClientProperty("active", active);
        for (Component c : tab.getComponents()) {
            if (c instanceof JLabel && "tabLabel".equals(c.getName())) {
                ((JLabel)c).setFont(active ? new Font("Segoe UI", Font.BOLD, 18) : AppTheme.FONT_BODY);
                ((JLabel)c).setForeground(active ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_MUTED);
            }
        }
        tab.repaint();
    }

    // ── Login form ────────────────────────────────────────────────────
    private JPanel buildLoginForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppTheme.BG_DARK);

        loginEmail = new UIComponents.ModernField("you@example.com", 20);
        loginPass  = new UIComponents.ModernPasswordField(20);

        loginEmail.setMaximumSize(new Dimension(360, AppTheme.INPUT_HEIGHT));
        loginPass.setMaximumSize(new Dimension(360, AppTheme.INPUT_HEIGHT));

        UIComponents.ModernButton loginBtn = new UIComponents.ModernButton("Sign In  →");
        loginBtn.setMaximumSize(new Dimension(360, AppTheme.BTN_HEIGHT));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());

        p.add(fieldGroup("Email address", loginEmail));
        p.add(Box.createVerticalStrut(14));
        p.add(fieldGroup("Password", loginPass));
        p.add(Box.createVerticalStrut(24));
        p.add(loginBtn);
        p.add(Box.createVerticalStrut(16));
        p.add(buildDemoHint());

        return p;
    }

    // ── Register form ─────────────────────────────────────────────────
    private JPanel buildRegisterForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppTheme.BG_DARK);

        regName  = new UIComponents.ModernField("Full Name", 20);
        regEmail = new UIComponents.ModernField("Email", 20);
        regPass  = new UIComponents.ModernPasswordField(20);
        regRole  = UIComponents.modernCombo(new String[]{"STUDENT", "TEACHER"});

        for (JComponent c : new JComponent[]{regName, regEmail, regPass, regRole}) {
            c.setMaximumSize(new Dimension(360, AppTheme.INPUT_HEIGHT));
        }

        UIComponents.ModernButton regBtn = new UIComponents.ModernButton("Create Account  →",
            AppTheme.SUCCESS, new Color(0x059669));
        regBtn.setMaximumSize(new Dimension(360, AppTheme.BTN_HEIGHT));
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.addActionListener(e -> doRegister());

        p.add(fieldGroup("Full name", regName));
        p.add(Box.createVerticalStrut(10));
        p.add(fieldGroup("Email", regEmail));
        p.add(Box.createVerticalStrut(10));
        p.add(fieldGroup("Password", regPass));
        p.add(Box.createVerticalStrut(10));
        p.add(fieldGroup("I am a...", regRole));
        p.add(Box.createVerticalStrut(20));
        p.add(regBtn);

        return p;
    }

    private JPanel fieldGroup(String label, JComponent field) {
        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.setBackground(AppTheme.BG_DARK);
        g.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_LABEL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        g.add(lbl);
        g.add(Box.createVerticalStrut(6));
        g.add(field);
        return g;
    }

    private JPanel buildDemoHint() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(AppTheme.BG_DARK);
        UIComponents.CardPanel card = new UIComponents.CardPanel(8);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel("Demo accounts:");
        title.setFont(AppTheme.FONT_LABEL);
        title.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel t = new JLabel("Teacher: teacher@demo.com / teacher123");
        t.setFont(AppTheme.FONT_SMALL);
        t.setForeground(AppTheme.TEXT_MUTED);

        JLabel s = new JLabel("Student: student@demo.com / student123");
        s.setFont(AppTheme.FONT_SMALL);
        s.setForeground(AppTheme.TEXT_MUTED);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(t);
        card.add(s);
        p.add(card);
        return p;
    }

    // ── Actions ───────────────────────────────────────────────────────
    private void doLogin() {
        String email = loginEmail.getText().trim();
        String pass  = new String(loginPass.getPassword());
        try {
            User user = service.login(email, pass);
            if (user == null) {
                errorLabel.setText("❌  Invalid email or password.");
            } else {
                if (listener != null) listener.onLoginSuccess(user);
            }
        } catch (Exception ex) {
            errorLabel.setText("❌  " + ex.getMessage());
        }
    }

    private void doRegister() {
        String name  = regName.getText().trim();
        String email = regEmail.getText().trim();
        String pass  = new String(regPass.getPassword());
        String role  = (String) regRole.getSelectedItem();
        try {
            User user = service.register(name, email, pass, role);
            if (listener != null) listener.onLoginSuccess(user);
        } catch (Exception ex) {
            errorLabel.setText("❌  " + ex.getMessage());
        }
    }
}
