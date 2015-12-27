package com.github.ksoichiro.cosole.reporter.writer

import com.github.ksoichiro.cosole.reporter.config.ReportConfig
import com.github.ksoichiro.cosole.reporter.report.Report

interface ReportWriter<R extends Report, C extends ReportConfig<R>> {
    void write(R report, C config)
}
