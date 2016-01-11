package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.CoberturaReport

class CoberturaReportConfig extends CoverageReportConfig<CoberturaReport> {
    CoberturaReportConfig() {
        coverageTaskName = 'generateCoberturaReport'
    }
}
