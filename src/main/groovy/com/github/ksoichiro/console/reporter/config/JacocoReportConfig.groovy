package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.JacocoReport

class JacocoReportConfig extends CoverageReportConfig<JacocoReport> {
    JacocoReportConfig() {
        coverageTaskName = 'jacocoTestReport'
    }
}
