package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.JacocoReport

class JacocoReportConfig implements ReportConfig<JacocoReport> {
    boolean enabled = true
    File reportFile

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
