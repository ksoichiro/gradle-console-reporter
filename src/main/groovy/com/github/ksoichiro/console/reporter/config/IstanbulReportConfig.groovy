package com.github.ksoichiro.console.reporter.config

import com.github.ksoichiro.console.reporter.report.IstanbulReport

class IstanbulReportConfig extends CoverageReportConfig<IstanbulReport> {
    IstanbulReportConfig() {
        boolean enabled = true
        onlyWhenCoverageTaskExecuted = false
    }
}
