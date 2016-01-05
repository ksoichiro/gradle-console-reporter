package com.github.ksoichiro.console.reporter

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConsoleReporterPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.create(ConsoleReporterExtension.NAME, ConsoleReporterExtension)
        target.tasks.create(ReportTestTask.NAME, ReportTestTask)
        target.tasks.create(ReportJacocoTask.NAME, ReportJacocoTask)
    }
}
