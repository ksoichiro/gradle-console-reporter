package com.github.ksoichiro.console.reporter

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConsoleReporterPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.rootProject.with {
            extensions.create(ConsoleReporterExtension.NAME, ConsoleReporterExtension)
            new ReportTestAction().hook(target)
            tasks.create(ReportCoverageTask.NAME, ReportCoverageTask)
        }
    }
}
