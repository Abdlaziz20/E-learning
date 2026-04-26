package com.miniProjectApp.presentation.components;

import  com.miniProjectApp.presentation.theme.AppTheme;
import  com.miniProjectApp.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Modern sidebar navigation panel
 */
public class Sidebar extends JPanel {

    public interface NavigationListener {
        void onNavigate(String destination);
    }

    private NavigationListener listener;
    private List<NavItem> items = new ArrayList<>();
    private String activeItem = "";
    private User currentUser;

    private static final Color ACTIVE_BG  = new Color(0x3B82F6, false);
    private static final Color HOVER_BG   = new Color(0x2D3F55);
    private static final Color ACTIVE_FG  = Color.WHITE;
    private static final Color INACTIVE_FG = AppTheme.TEXT_SECONDARY;

    public Sidebar(User user, NavigationListener listener) {
        this.currentUser = user;
        this.listener = listener;

        setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 0));
        setBackground(AppTheme.BG_SIDEBAR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER));

        // Content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_SIDEBAR);
        content.setOpaque(true);

        // Logo area
        content.add(buildLogo());
        content.add(Box.createVerticalStrut(8));
        content.add(buildSeparator());
        content.add(Box.createVerticalStrut(8));

        // User profile
        content.add(buildUserProfile());
        content.add(Box.createVerticalStrut(8));
        content.add(buildSeparator());
        content.add(Box.createVerticalStrut(12));

        // Nav section label
        content.add(buildSectionLabel("NAVIGATION"));
        content.add(Box.createVerticalStrut(4));

        // Nav items – set by caller
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(AppTheme.BG_SIDEBAR);
        navPanel.setOpaque(true);
        navPanel.setName("navPanel");
        content.add(navPanel);

        content.add(Box.createVerticalGlue());

        // Logout button at bottom
        content.add(buildSeparator());
        content.add(Box.createVerticalStrut(8));
        content.add(buildLogoutButton());
        content.add(Box.createVerticalStrut(16));

        add(content, BorderLayout.CENTER);
    }

    public void addNavItem(String icon, String label, String destination) {
        items.add(new NavItem(icon, label, destination));
        JPanel navPanel = findNavPanel();
        if (navPanel != null) {
            navPanel.add(buildNavButton(icon, label, destination));
            navPanel.revalidate();
        }
    }

    public void setActiveItem(String destination) {
        this.activeItem = destination;
        refresh();
    }

    private JPanel findNavPanel() {
        for (Component c : ((JPanel)((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.CENTER))
                .getComponents()) {
            if (c instanceof JPanel && "navPanel".equals(((JPanel)c).getName())) {
                return (JPanel)c;
            }
            if (c instanceof JPanel) {
                for (Component cc : ((JPanel)c).getComponents()) {
                    if (cc instanceof JPanel && "navPanel".equals(((JPanel)cc).getName()))
                        return (JPanel)cc;
                }
            }
        }
        return null;
    }

    private JPanel buildLogo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        p.setBackground(AppTheme.BG_SIDEBAR);
        p.setOpaque(true);

        // E icon
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT);
                g2.fillRoundRect(0,0,32,32,8,8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("E", (32 - fm.stringWidth("E"))/2, 22);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(32,32); }
        };
        icon.setOpaque(false);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(AppTheme.BG_SIDEBAR);
        text.setOpaque(true);

        JLabel title = new JLabel("EduLearn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Learning Platform");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);

        text.add(title);
        text.add(sub);

        p.add(icon);
        p.add(text);
        return p;
    }

    private JPanel buildUserProfile() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        p.setBackground(new Color(0x1E293B));
        p.setOpaque(true);
        p.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 64));

        Color avatarColor;
        try {
            avatarColor = Color.decode(currentUser.getAvatarColor() != null
                ? currentUser.getAvatarColor() : "#4A90E2");
        } catch (Exception e) {
            avatarColor = AppTheme.ACCENT;
        }

        JPanel avatar = UIComponents.avatarCircle(currentUser.getInitials(), avatarColor, 36);
        p.add(avatar);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(new Color(0x1E293B));
        info.setOpaque(true);

        JLabel name = new JLabel(currentUser.getFullName());
        name.setFont(AppTheme.FONT_LABEL);
        name.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel role = new JLabel(currentUser.getRole().name());
        role.setFont(AppTheme.FONT_SMALL);
        role.setForeground(currentUser.getRole() == User.Role.TEACHER
            ? AppTheme.PURPLE : AppTheme.ACCENT);

        info.add(name);
        info.add(role);
        p.add(info);

        return p;
    }

    private JPanel buildNavButton(String icon, String label, String destination) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (destination.equals(activeItem)) {
                    g2.setColor(new Color(AppTheme.ACCENT.getRed(), AppTheme.ACCENT.getGreen(),
                        AppTheme.ACCENT.getBlue(), 25));
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 8, 8);
                    // Left accent bar
                    g2.setColor(AppTheme.ACCENT);
                    g2.fillRoundRect(4, 6, 3, getHeight()-12, 3, 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 44));
        btn.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 44));
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setName("nav_" + destination);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLbl.setForeground(destination.equals(activeItem) ? AppTheme.ACCENT : INACTIVE_FG);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(destination.equals(activeItem)
            ? new Font("Segoe UI", Font.BOLD, 13) : AppTheme.FONT_BODY);
        textLbl.setForeground(destination.equals(activeItem) ? ACTIVE_FG : INACTIVE_FG);

        btn.add(iconLbl);
        btn.add(textLbl);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!destination.equals(activeItem)) btn.setBackground(HOVER_BG);
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(null);
                btn.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                activeItem = destination;
                refresh();
                if (listener != null) listener.onNavigate(destination);
            }
        });

        return btn;
    }

    private void refresh() {
        JPanel navPanel = findNavPanel();
        if (navPanel == null) return;
        navPanel.removeAll();
        for (NavItem item : items) {
            navPanel.add(buildNavButton(item.icon, item.label, item.destination));
        }
        navPanel.revalidate();
        navPanel.repaint();
    }

    private JPanel buildLogoutButton() {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 48));

        JLabel icon = new JLabel("🚪");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel lbl = new JLabel("Logout");
        lbl.setFont(AppTheme.FONT_BODY);
        lbl.setForeground(new Color(0xEF4444));

        btn.add(icon);
        btn.add(lbl);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (listener != null) listener.onNavigate("LOGOUT");
            }
        });
        return btn;
    }

    private JPanel buildSeparator() {
        JPanel p = new JPanel();
        p.setBackground(AppTheme.BORDER);
        p.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 1));
        p.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 1));
        return p;
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(AppTheme.TEXT_MUTED);
        lbl.setMaximumSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 24));
        lbl.setBorder(new EmptyBorder(0, 12, 0, 0));
        return lbl;
    }

    private static class NavItem {
        String icon, label, destination;
        NavItem(String i, String l, String d) { icon=i; label=l; destination=d; }
    }
}
