package com.miniProjectApp.presentation.theme;

import java.awt.*;

/**
 * Modern UI Theme Constants
 * Clean dark-navy + electric blue palette
 */
public class AppTheme {

    // ── PALETTE ──────────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(0x0F172A);   // main background
    public static final Color BG_CARD       = new Color(0x1E293B);   // cards / panels
    public static final Color BG_SIDEBAR    = new Color(0x0F172A);   // sidebar
    public static final Color BG_HOVER      = new Color(0x2D3F55);   // hover state
    public static final Color BG_INPUT      = new Color(0x1E293B);   // input fields
    public static final Color BG_TABLE_ROW  = new Color(0x162032);   // table alt row

    public static final Color ACCENT        = new Color(0x3B82F6);   // electric blue
    public static final Color ACCENT_LIGHT  = new Color(0x60A5FA);   // lighter blue
    public static final Color ACCENT_DARK   = new Color(0x1D4ED8);   // darker blue

    public static final Color SUCCESS       = new Color(0x10B981);   // green
    public static final Color WARNING       = new Color(0xF59E0B);   // amber
    public static final Color DANGER        = new Color(0xEF4444);   // red
    public static final Color PURPLE        = new Color(0x8B5CF6);   // purple

    public static final Color TEXT_PRIMARY  = new Color(0xF1F5F9);   // main text
    public static final Color TEXT_SECONDARY= new Color(0x94A3B8);   // muted text
    public static final Color TEXT_MUTED    = new Color(0x64748B);   // very muted
    public static final Color BORDER        = new Color(0x334155);   // borders
    public static final Color BORDER_FOCUS  = new Color(0x3B82F6);   // focused border

    // ── FONTS ────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FONT_HEADING   = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_SUBHEAD   = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_MONO      = new Font("Consolas",  Font.PLAIN, 13);
    public static final Font FONT_LABEL     = new Font("Segoe UI", Font.BOLD,  12);

    // ── SIZES ────────────────────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH   = 220;
    public static final int HEADER_HEIGHT   = 64;
    public static final int BTN_HEIGHT      = 40;
    public static final int INPUT_HEIGHT    = 40;
    public static final int RADIUS          = 10;

    // ── CATEGORY COLORS ──────────────────────────────────────────────────
    public static final Color[] CAT_COLORS = {
        new Color(0x3B82F6), new Color(0x8B5CF6), new Color(0x10B981),
        new Color(0xF59E0B), new Color(0xEF4444), new Color(0x06B6D4),
        new Color(0xEC4899), new Color(0x84CC16)
    };

    public static Color getCategoryColor(int index) {
        return CAT_COLORS[index % CAT_COLORS.length];
    }
}
