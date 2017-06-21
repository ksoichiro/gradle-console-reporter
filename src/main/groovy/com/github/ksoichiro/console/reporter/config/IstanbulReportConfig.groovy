package com.github.ksoichiro.console.reporter.config

class IstanbulReportConfig extends CoverageReportConfig {
    IstanbulReportConfig() {
        onlyWhenCoverageTaskExecuted = false
    }
}
