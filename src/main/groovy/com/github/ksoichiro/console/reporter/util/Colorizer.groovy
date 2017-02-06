package com.github.ksoichiro.console.reporter.util

class Colorizer {
    static green(message) {
        fgColor(FgColor.GREEN, message)
    }

    static yellow(message) {
        fgColor(FgColor.YELLOW, message)
    }

    static red(message) {
        fgColor(FgColor.RED, message)
    }

    static fgColor(FgColor color, message) {
        "\u001b[${color.code}m${message}\u001b[39m"
    }
}
