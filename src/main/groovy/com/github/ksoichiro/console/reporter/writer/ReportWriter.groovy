package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.ReportConfig
import com.github.ksoichiro.console.reporter.report.Report
import org.gradle.api.Project

abstract class ReportWriter<R extends Report, C extends ReportConfig> {
    PrintWriter writer

    void println() {
        println ""
    }

    void println(Object value) {
        if (writer == null) {
            super.println value
        } else {
            writer.println value
        }
    }
}
