package com.github.ksoichiro.console.reporter.config

abstract class ReportConfig {
    boolean colorEnabled = true

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
