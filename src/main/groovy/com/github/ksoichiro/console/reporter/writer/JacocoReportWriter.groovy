package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.JacocoReportConfig
import com.github.ksoichiro.console.reporter.report.JacocoReport

class JacocoReportWriter implements ReportWriter<JacocoReport, JacocoReportConfig> {
    @Override
    void write(JacocoReport report, JacocoReportConfig config) {
        println String.format("C0 Coverage: %.1f%%", report.c0Coverage)
    }
}
