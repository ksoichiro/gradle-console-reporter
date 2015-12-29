package com.github.ksoichiro.console.reporter.parser

import com.github.ksoichiro.console.reporter.config.ReportConfig
import com.github.ksoichiro.console.reporter.report.Report
import org.gradle.api.Project

interface ReportParser<R extends Report, C extends ReportConfig<R>> {
    R parse(Project project, C config)
}
