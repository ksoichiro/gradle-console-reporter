package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.config.JUnitReportConfig
import com.github.ksoichiro.console.reporter.config.JacocoReportConfig
import org.gradle.api.GradleException
import org.gradle.util.ConfigureUtil

class ConsoleReporterExtension {
    public static final NAME = 'consoleReporter'
    JUnitReportConfig junit
    JacocoReportConfig jacoco

    ConsoleReporterExtension() {
        junit = new JUnitReportConfig()
        jacoco = new JacocoReportConfig()
    }

    def methodMissing(String name, def args) {
        if (metaClass.hasProperty(this, name)) {
            return ConfigureUtil.configure(args[0] as Closure, this."$name")
        } else {
            throw new GradleException("Missing method: ${name}")
        }
    }
}
