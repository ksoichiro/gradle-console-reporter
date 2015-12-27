package com.github.ksoichiro.cosole.reporter

import org.gradle.api.GradleException
import org.gradle.util.ConfigureUtil

class ConsoleReporterExtension {
    public static final NAME = 'consoleReporter'
    JUnitReport junit

    ConsoleReporterExtension() {
        junit = new JUnitReport()
    }

    def methodMissing(String name, def args) {
        if (metaClass.hasProperty(this, name)) {
            return ConfigureUtil.configure(args[0] as Closure, this."$name")
        } else {
            throw new GradleException("Missing method: ${name}")
        }
    }
}
