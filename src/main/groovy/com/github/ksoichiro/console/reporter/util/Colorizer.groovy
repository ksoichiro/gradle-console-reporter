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
        isTerminal() ? "\u001b[${color.code}m${message}\u001b[39m" : message
    }

    static boolean isTerminal() {
        // With Gradle daemon, this is always false.
        // https://github.com/gradle/gradle/issues/1251
        System.console() != null
        // The following code is equivalent (and also not working on daemon)
//        ConsoleDetector consoleDetector = NativeServices.getInstance().get(ConsoleDetector)
//        ConsoleMetaData consoleMetaData = consoleDetector.getConsole()
//        if (consoleMetaData == null) {
//            return false
//        }
//        consoleMetaData.isStdOut()
    }
}
