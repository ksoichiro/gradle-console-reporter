package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.Report

abstract class ReportConfig<R extends Report> {
    boolean colorEnabled = true

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
