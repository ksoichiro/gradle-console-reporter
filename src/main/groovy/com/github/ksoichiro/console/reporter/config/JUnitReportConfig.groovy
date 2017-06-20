package com.github.ksoichiro.console.reporter.config

class JUnitReportConfig extends ReportConfig {
    boolean enabled = true
    boolean reportOnFailure = true
    boolean summaryEnabled = false
    boolean stdoutEnabled = false
    boolean stderrEnabled = false
    boolean stacktraceEnabled = true
    boolean partialSourceInsertionEnabled = true
}
