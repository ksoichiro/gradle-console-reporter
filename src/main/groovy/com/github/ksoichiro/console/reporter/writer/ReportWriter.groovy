package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.ReportConfig
import com.github.ksoichiro.console.reporter.report.Report
import org.gradle.api.Project

interface ReportWriter<R extends Report, C extends ReportConfig<R>> {
    void write(Project project, R report, C config)
}
