package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.CoverageReport

abstract class CoverageReportConfig<R extends CoverageReport> extends ReportConfig<R> {
    boolean enabled = true
    boolean onlyWhenCoverageTaskExecuted = true
    boolean reportAfterBuildFinished = true
    boolean failIfLessThanThresholdError = false
    boolean autoconfigureCoverageConfig = true
    String coverageTaskName
    File reportFile
    int thresholdFine = 90
    int thresholdWarning = 70
    int thresholdError = 0
    String brokenCoverageErrorMessage = "Coverage has fallen below the threshold in some projects."
}
