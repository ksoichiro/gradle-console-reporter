package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.parser.CoberturaReportParser
import com.github.ksoichiro.console.reporter.report.CoberturaReport
import com.github.ksoichiro.console.reporter.writer.CoberturaReportWriter
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskState

class ReportCoberturaTask extends DefaultTask {
    public static String NAME = 'reportCobertura'
    ConsoleReporterExtension extension

    ReportCoberturaTask() {
        project.afterEvaluate {
            extension = project.extensions."${ConsoleReporterExtension.NAME}"

            project.gradle.taskGraph.afterTask { Task task, TaskState state ->
                if (task.name == extension.cobertura.coverageTaskName && task.project == project) {
                    execute()
                }
            }
        }
    }

    @TaskAction
    void exec() {
        if (extension.cobertura.enabled) {
            reportCobertura()
        }
    }

    void reportCobertura() {
        CoberturaReport report = new CoberturaReportParser().parse(project, extension.cobertura)
        new CoberturaReportWriter().write(project, report, extension.cobertura)
    }
}
