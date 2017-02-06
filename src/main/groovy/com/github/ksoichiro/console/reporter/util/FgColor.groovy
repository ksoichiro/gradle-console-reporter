package com.github.ksoichiro.console.reporter.util

enum FgColor {
    WHITE(37),
    BLACK(30),
    BLUE(34),
    CYAN(36),
    GREEN(32),
    MAGENTA(35),
    RED(31),
    YELLOW(33),
    BRIGHT_BLACK(90),
    BRIGHT_RED(91),
    BRIGHT_GREEN(92),
    BRIGHT_YELLOW(93),
    BRIGHT_BLUE(94),
    BRIGHT_MAGENTA(95),
    BRIGHT_CYAN(96),
    BRIGHT_WHITE(97),

    final int code

    FgColor(int code) {
        this.code = code
    }
}
