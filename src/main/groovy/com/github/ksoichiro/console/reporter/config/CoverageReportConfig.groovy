package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.CoverageReport

abstract class CoverageReportConfig<R extends CoverageReport> implements ReportConfig<R> {
    boolean enabled = true
    boolean onlyWhenCoverageTaskExecuted = true
    boolean reportAfterBuildFinished = true
    boolean failIfLessThanThresholdError = false
    String coverageTaskName
    File reportFile
    int thresholdFine = 90
    int thresholdWarning = 70
    int thresholdError = 0
    String brokenCoverageErrorMessage = "Coverage has fallen below the threshold in some projects."

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
