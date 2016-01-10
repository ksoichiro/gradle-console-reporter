package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.CoverageReport

abstract class CoverageReportConfig<R extends CoverageReport> implements ReportConfig<R> {
    boolean enabled = true
    boolean onlyWhenCoverageTaskExecuted = true
    boolean reportAfterBuildFinished = true
    String coverageTaskName
    File reportFile
    int thresholdFine = 90
    int thresholdWarning = 70

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
