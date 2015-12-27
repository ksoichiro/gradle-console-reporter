package com.github.ksoichiro.cosole.reporter

import com.github.ksoichiro.cosole.reporter.config.JUnitReportConfig
import org.gradle.api.GradleException
import org.gradle.util.ConfigureUtil

class ConsoleReporterExtension {
    public static final NAME = 'consoleReporter'
    JUnitReportConfig junit

    ConsoleReporterExtension() {
        junit = new JUnitReportConfig()
    }

    def methodMissing(String name, def args) {
        if (metaClass.hasProperty(this, name)) {
            return ConfigureUtil.configure(args[0] as Closure, this."$name")
        } else {
            throw new GradleException("Missing method: ${name}")
        }
    }
}
