package com.github.ksoichiro.cosole.reporter

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConsoleReporterPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.create(ConsoleReporterExtension.NAME, ConsoleReporterExtension)
        target.tasks.create(ReportTestTask.NAME, ReportTestTask)
    }
}
