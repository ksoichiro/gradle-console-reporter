package com.github.ksoichiro.console.reporter.config

class JacocoReportConfig extends CoverageReportConfig {
    JacocoReportConfig() {
        coverageTaskName = 'jacocoTestReport'
    }
}
