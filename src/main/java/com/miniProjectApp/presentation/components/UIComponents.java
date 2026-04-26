package com.miniProjectApp.presentation.components;

import com.miniProjectApp.presentation.theme.AppTheme;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class UIComponents {

    // ══════════════════════════════════════════════════════
    //  MODERN BUTTON
    // ══════════════════════════════════════════════════════
    public static class ModernButton extends JButton {
        private Color bgColor;
        private Color hoverColor;
        private boolean isHovering = false;
        private boolean isPrimary;

        public ModernButton(String text, Color bg, Color hover) {
            super(text);
            this.bgColor = bg;
            this.hoverColor = hover;
            this.isPrimary = true;
            setup();
        }

        public ModernButton(String text) {
            this(text, AppTheme.ACCENT, AppTheme.ACCENT_DARK);
        }

        public static ModernButton secondary(String text) {
            ModernButton btn = new ModernButton(text, AppTheme.BG_HOVER, new Color(0x3D5068));
            btn.setForeground(AppTheme.TEXT_PRIMARY);
            return btn;
        }

        public static ModernButton danger(String text) {
            return new ModernButton(text, AppTheme.DANGER, new Color(0xDC2626));
        }

        public static ModernButton success(String text) {
            return new ModernButton(text, AppTheme.SUCCESS, new Color(0x059669));
        }

        private void setup() {
            setFont(AppTheme.FONT_BUTTON);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 20, 8, 20));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovering = true; repaint(); }
                public void mouseExited(MouseEvent e)  { isHovering = false; repaint(); }
            });
        }


        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(AppTheme.FONT_BUTTON);
            int textWidth = fm.stringWidth(getText());
            // 20px padding gauche + 20px padding droite + 8px marge sécurité
            return new Dimension(textWidth + 48, AppTheme.BTN_HEIGHT);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color current = isHovering ? hoverColor : bgColor;
            g2.setColor(current);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.RADIUS, AppTheme.RADIUS);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════
    //  MODERN TEXT FIELD
    // ══════════════════════════════════════════════════════
    public static class ModernField extends JTextField {
        private String placeholder;
        private boolean focused = false;

        public ModernField(String placeholder, int cols) {
            super(cols);
            this.placeholder = placeholder;
            setup();
        }

        private void setup() {
            setFont(AppTheme.FONT_BODY);
            setForeground(AppTheme.TEXT_PRIMARY);
            setBackground(AppTheme.BG_INPUT);
            setCaretColor(AppTheme.ACCENT_LIGHT);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setPreferredSize(new Dimension(getPreferredSize().width, AppTheme.INPUT_HEIGHT));

            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { focused = true; repaint(); }
                public void focusLost(FocusEvent e)   { focused = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppTheme.BG_INPUT);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            Color borderCol = focused ? AppTheme.BORDER_FOCUS : AppTheme.BORDER;
            g2.setColor(borderCol);
            g2.setStroke(new BasicStroke(focused ? 2f : 1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            g2.dispose();
            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D gh = (Graphics2D) g.create();
                gh.setFont(AppTheme.FONT_BODY);
                gh.setColor(AppTheme.TEXT_MUTED);
                gh.drawString(placeholder, 14, getHeight()/2 + 5);
                gh.dispose();
            }
        }
    }

    // ══════════════════════════════════════════════════════
    //  MODERN PASSWORD FIELD
    // ══════════════════════════════════════════════════════
    public static class ModernPasswordField extends JPasswordField {
        private boolean focused = false;

        public ModernPasswordField(int cols) {
            super(cols);
            setFont(AppTheme.FONT_BODY);
            setForeground(AppTheme.TEXT_PRIMARY);
            setBackground(AppTheme.BG_INPUT);
            setCaretColor(AppTheme.ACCENT_LIGHT);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setPreferredSize(new Dimension(getPreferredSize().width, AppTheme.INPUT_HEIGHT));

            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { focused = true; repaint(); }
                public void focusLost(FocusEvent e)   { focused = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppTheme.BG_INPUT);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            Color borderCol = focused ? AppTheme.BORDER_FOCUS : AppTheme.BORDER;
            g2.setColor(borderCol);
            g2.setStroke(new BasicStroke(focused ? 2f : 1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════
    //  CARD PANEL
    // ══════════════════════════════════════════════════════
    public static class CardPanel extends JPanel {
        private int radius;
        private Color borderColor;
        private boolean shadow;

        public CardPanel(int radius) {
            this.radius = radius;
            this.borderColor = AppTheme.BORDER;
            this.shadow = true;
            setOpaque(false);
            setBackground(AppTheme.BG_CARD);
        }

        public CardPanel() { this(12); }

        public void setBorderColor(Color c) { this.borderColor = c; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (shadow) {
                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(3, 4, getWidth()-6, getHeight()-4, radius, radius);
            }
            g2.setColor(AppTheme.BG_CARD);
            g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, radius, radius);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════
    //  STYLED TABLE
    // ══════════════════════════════════════════════════════
    public static JTable createTable(String[] cols) {
        JTable table = new JTable(new Object[0][cols.length], cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(table);
        return table;
    }

    public static void styleTable(JTable table) {
        table.setBackground(AppTheme.BG_CARD);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0x3B82F6, false));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(AppTheme.BORDER);
        table.setFillsViewportHeight(true);
        table.setOpaque(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0x0F172A));
        header.setForeground(AppTheme.TEXT_SECONDARY);
        header.setFont(AppTheme.FONT_LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean selected,
                                                           boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                setFont(AppTheme.FONT_BODY);
                setForeground(selected ? Color.WHITE : AppTheme.TEXT_PRIMARY);
                setBackground(selected ? AppTheme.ACCENT :
                        (row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_TABLE_ROW));
                setBorder(new EmptyBorder(4, 12, 4, 12));
                return this;
            }
        });
    }

    public static JScrollPane wrapTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        sp.setBackground(AppTheme.BG_CARD);
        sp.getViewport().setBackground(AppTheme.BG_CARD);
        sp.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        return sp;
    }

    // ══════════════════════════════════════════════════════
    //  STAT CARD
    // ══════════════════════════════════════════════════════
    public static JPanel statCard(String label, String value, Color accent) {
        CardPanel card = new CardPanel(14);
        card.setBorderColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80));
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillRoundRect(0,0,40,4,4,4);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(40, 8); }
        };
        bar.setOpaque(false);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);

        card.add(bar, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);

        return card;
    }

    // ══════════════════════════════════════════════════════
    //  SECTION HEADER
    // ══════════════════════════════════════════════════════
    public static JPanel sectionHeader(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(AppTheme.FONT_HEADING);
        t.setForeground(AppTheme.TEXT_PRIMARY);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel s = new JLabel(subtitle);
            s.setFont(AppTheme.FONT_BODY);
            s.setForeground(AppTheme.TEXT_SECONDARY);
            s.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(Box.createVerticalStrut(2));
            p.add(s);
        }
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  COMBO BOX
    // ══════════════════════════════════════════════════════
    public static JComboBox<String> modernCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(AppTheme.FONT_BODY);
        combo.setBackground(AppTheme.BG_INPUT);
        combo.setForeground(AppTheme.TEXT_PRIMARY);
        combo.setPreferredSize(new Dimension(200, AppTheme.INPUT_HEIGHT));
        combo.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? AppTheme.ACCENT : AppTheme.BG_INPUT);
                setForeground(AppTheme.TEXT_PRIMARY);
                setFont(AppTheme.FONT_BODY);
                setBorder(new EmptyBorder(6, 12, 6, 12));
                return this;
            }
        });
        return combo;
    }

    // ══════════════════════════════════════════════════════
    //  AVATAR CIRCLE
    // ══════════════════════════════════════════════════════
    public static JPanel avatarCircle(String initials, Color color, int size) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, size, size);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, size/3));
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth(initials)) / 2;
                int y = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
        };
        p.setOpaque(false);
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  TEXT AREA
    // ══════════════════════════════════════════════════════
    public static JTextArea modernTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(AppTheme.FONT_BODY);
        ta.setForeground(AppTheme.TEXT_PRIMARY);
        ta.setBackground(AppTheme.BG_INPUT);
        ta.setCaretColor(AppTheme.ACCENT_LIGHT);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(10, 12, 10, 12));
        return ta;
    }

    public static JScrollPane wrapTextArea(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        sp.setBackground(AppTheme.BG_INPUT);
        return sp;
    }

    // ══════════════════════════════════════════════════════
    //  FORM ROW helper
    // ══════════════════════════════════════════════════════
    public static JPanel formRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AppTheme.FONT_LABEL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(140, AppTheme.INPUT_HEIGHT));
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    // ══════════════════════════════════════════════════════
    //  SCROLL BAR UI (minimal)
    // ══════════════════════════════════════════════════════
    public static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = AppTheme.BORDER;
            trackColor = AppTheme.BG_DARK;
        }
        @Override protected JButton createDecreaseButton(int o) { return invisBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return invisBtn(); }
        private JButton invisBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6);
            g2.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    // ══════════════════════════════════════════════════════
    //  BADGE / CHIP
    // ══════════════════════════════════════════════════════
    public static JLabel badge(String text, Color bg) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(bg);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(bg);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        lbl.setOpaque(false);
        return lbl;
    }

    // ══════════════════════════════════════════════════════
    //  SEPARATOR
    // ══════════════════════════════════════════════════════
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        sep.setBackground(AppTheme.BORDER);
        return sep;
    }

    // ══════════════════════════════════════════════════════
    //  NOTIFICATION / TOAST
    // ══════════════════════════════════════════════════════
    public static void showToast(JFrame frame, String msg, boolean success) {
        Color bg = success ? new Color(0x10B981) : new Color(0xEF4444);
        JWindow toast = new JWindow(frame);
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        JLabel lbl = new JLabel((success ? "✓  " : "✕  ") + msg);
        lbl.setFont(AppTheme.FONT_BUTTON);
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(12, 20, 12, 20));
        p.add(lbl);
        toast.add(p);
        toast.pack();
        Point loc = frame.getLocationOnScreen();
        toast.setLocation(loc.x + frame.getWidth()/2 - toast.getWidth()/2,
                loc.y + frame.getHeight() - 80);
        toast.setVisible(true);
        new Timer(2500, e -> { toast.setVisible(false); toast.dispose(); }).start();
    }
}