package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.parser.JacocoReportParser
import com.github.ksoichiro.console.reporter.report.JacocoReport
import com.github.ksoichiro.console.reporter.writer.JacocoReportWriter
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskState

class ReportJacocoTask extends DefaultTask {
    public static String NAME = 'reportJacoco'
    ConsoleReporterExtension extension

    ReportJacocoTask() {
        project.afterEvaluate {
            extension = project.extensions."${ConsoleReporterExtension.NAME}"

            if (extension.jacoco.autoconfigureCoverageConfig) {
                // Enable XML report carefully and silently
                if (project.plugins.hasPlugin('jacoco')) {
                    // jacocoTestReport is not an extension
                    try {
                        def jacocoExtension = project.jacocoTestReport
                        if (jacocoExtension && jacocoExtension.reports?.xml != null) {
                            jacocoExtension.reports.xml.enabled = true
                        }
                    } catch (MissingPropertyException ignore) {
                    }
                }
            }

            project.gradle.taskGraph.afterTask { Task task, TaskState state ->
                if (task instanceof org.gradle.testing.jacoco.tasks.JacocoReport && task.project == project) {
                    execute()
                }
            }
        }
    }

    @TaskAction
    void exec() {
        if (extension.jacoco.enabled) {
            reportJacoco()
        }
    }

    void reportJacoco() {
        JacocoReport report = new JacocoReportParser().parse(project, extension.jacoco)
        new JacocoReportWriter().write(project, report, extension.jacoco)
    }
}
