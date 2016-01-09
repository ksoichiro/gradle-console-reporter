package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.CoberturaReport

class CoberturaReportConfig implements ReportConfig<CoberturaReport> {
    boolean enabled = true
    boolean onlyWhenCoberturaTaskExecuted = true
    boolean reportAfterBuildFinished = true
    String coberturaTaskName = 'cobertura'
    File reportFile
    int thresholdFine = 90
    int thresholdWarning = 70

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
