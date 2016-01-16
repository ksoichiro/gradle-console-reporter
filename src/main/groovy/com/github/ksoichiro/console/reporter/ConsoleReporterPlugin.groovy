package com.github.ksoichiro.console.reporter

import org.fusesource.jansi.Ansi
import org.gradle.api.Plugin
import org.gradle.api.Project

class ConsoleReporterPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.create(ConsoleReporterExtension.NAME, ConsoleReporterExtension)
        target.tasks.create(ReportTestTask.NAME, ReportTestTask)
        target.tasks.create(ReportJacocoTask.NAME, ReportJacocoTask)
        target.tasks.create(ReportCoberturaTask.NAME, ReportCoberturaTask)

        // fgBright is not available in the last release, so add it dynamically
        Ansi.metaClass.fgBright { Ansi.Color color ->
            delegate.attributeOptions.add(Integer.valueOf(color.fgBright()))
            delegate
        }
    }
}
