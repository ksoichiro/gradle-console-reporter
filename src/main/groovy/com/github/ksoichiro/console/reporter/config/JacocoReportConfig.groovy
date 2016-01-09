package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.JacocoReport

class JacocoReportConfig implements ReportConfig<JacocoReport> {
    boolean enabled = true
    boolean onlyWhenJacocoTaskExecuted = true
    boolean reportAfterBuildFinished = true
    String jacocoTaskName = 'jacocoTestReport'
    File reportFile
    int thresholdFine = 90
    int thresholdWarning = 70

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
