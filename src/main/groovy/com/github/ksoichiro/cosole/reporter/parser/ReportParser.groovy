package com.github.ksoichiro.cosole.reporter.parser

import com.github.ksoichiro.cosole.reporter.config.ReportConfig
import com.github.ksoichiro.cosole.reporter.report.Report
import org.gradle.api.Project

interface ReportParser<R extends Report, C extends ReportConfig<R>> {
    R parse(Project project, C config)
}
