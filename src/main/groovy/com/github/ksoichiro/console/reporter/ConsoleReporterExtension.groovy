package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.config.CoberturaReportConfig
import com.github.ksoichiro.console.reporter.config.IstanbulReportConfig
import com.github.ksoichiro.console.reporter.config.JUnitReportConfig
import com.github.ksoichiro.console.reporter.config.JacocoReportConfig
import com.github.ksoichiro.console.reporter.config.TotalReportConfig
import com.github.ksoichiro.console.reporter.report.TotalReport
import org.gradle.api.GradleException
import org.gradle.util.ConfigureUtil

class ConsoleReporterExtension {
    public static final NAME = 'consoleReporter'
    TotalReportConfig total
    JUnitReportConfig junit
    JacocoReportConfig jacoco
    CoberturaReportConfig cobertura
    IstanbulReportConfig istanbul

    ConsoleReporterExtension() {
        total = new TotalReportConfig()
        junit = new JUnitReportConfig()
        jacoco = new JacocoReportConfig()
        cobertura = new CoberturaReportConfig()
        istanbul = new IstanbulReportConfig()
    }

    def methodMissing(String name, def args) {
        if (metaClass.hasProperty(this, name)) {
            return ConfigureUtil.configure(args[0] as Closure, this."$name")
        } else {
            throw new GradleException("Missing method: ${name}")
        }
    }
}
