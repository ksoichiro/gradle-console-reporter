package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.JUnitReport

class JUnitReportConfig implements ReportConfig<JUnitReport> {
    boolean enabled = true
    boolean reportOnFailure = true
    boolean summaryEnabled = false
    boolean stdoutEnabled = false
    boolean stderrEnabled = false
    boolean stacktraceEnabled = true

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
