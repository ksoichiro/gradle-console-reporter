package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.ReportConfig
import com.github.ksoichiro.console.reporter.report.Report

interface ReportWriter<R extends Report, C extends ReportConfig<R>> {
    void write(R report, C config)
}
