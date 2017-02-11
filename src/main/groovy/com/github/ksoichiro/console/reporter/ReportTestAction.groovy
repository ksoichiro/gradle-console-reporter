package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.parser.JUnitReportParser
import com.github.ksoichiro.console.reporter.writer.JUnitReportWriter
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.testing.Test

class ReportTestAction {
    public static String NAME = 'reportTest'
    ConsoleReporterExtension extension
    Project project

    void hook(Project project) {
        this.project = project
        project.afterEvaluate {
            extension = project.extensions."${ConsoleReporterExtension.NAME}"

            if (extension.junit.enabled && extension.junit.reportOnFailure) {
                project.gradle.taskGraph.afterTask { Task task, TaskState state ->
                    if (task instanceof Test && state.failure) {
                        reportJUnit([task] as Set<Test>)
                    }
                }
            }
        }
    }

    void reportJUnit(Set<Test> testTask) {
        def reports = [(project): new JUnitReportParser(testTask).parse(project, extension.junit)]
        new JUnitReportWriter().write(project, reports, extension.junit)
    }
}
