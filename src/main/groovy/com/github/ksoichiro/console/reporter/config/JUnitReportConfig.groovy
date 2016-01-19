package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.JUnitReport

class JUnitReportConfig extends ReportConfig<JUnitReport> {
    boolean enabled = true
    boolean reportOnFailure = true
    boolean summaryEnabled = false
    boolean stdoutEnabled = false
    boolean stderrEnabled = false
    boolean stacktraceEnabled = true
    boolean partialSourceInsertionEnabled = true
}
