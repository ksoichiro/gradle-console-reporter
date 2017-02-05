package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.CoverageReportConfig
import com.github.ksoichiro.console.reporter.report.CoverageReport
//import org.fusesource.jansi.Ansi
//import org.fusesource.jansi.AnsiConsole
import org.gradle.api.GradleException
import org.gradle.api.Project

//import static org.fusesource.jansi.Ansi.Color.*
//import static org.fusesource.jansi.Ansi.ansi

abstract class CoverageReportWriter<R extends CoverageReport, C extends CoverageReportConfig<R>> extends ReportWriter<R, C> {
    Project project
    boolean colorEnabled
    R report
    C config

    @Override
    void write(Project project, R report, C config) {
        this.project = project
        this.report = report
        this.config = config
        this.colorEnabled = config.colorEnabled
        if (config.reportAfterBuildFinished) {
            def headers = headerForFirstSubproject()
            def result = toAnsi("${adjustedProjectName()} ${rightAlignedCoverage(report.c0Coverage)}")
            project.gradle.buildFinished writeResult(headers, result)
        } else {
            println toAnsi("C0 Coverage: ${rightAlignedCoverage(report.c0Coverage)}")
        }
        if (config.failIfLessThanThresholdError) {
            if (report.c0Coverage < config.thresholdError) {
                project.gradle.buildFinished {
                    throw new GradleException(config.brokenCoverageErrorMessage)
                }
            }
        }
    }

    def headerForFirstSubproject() {
        if (project.is(project.rootProject)
            || 0 == projectsIncludedInTaskGraph().findIndexOf { it.name == project.name }) {
            return ["", "Coverage summary:"]
        }
        null
    }

    def toAnsi(def message) {
//        if (colorEnabled) {
//            AnsiConsole.systemInstall()
//            def result = ansi()
//                .fg(styleForQuality(report.c0Coverage))
//                .a(message)
//                .reset()
//            AnsiConsole.systemUninstall()
//            result
//        } else {
            message
//        }
    }

//    Ansi.Color styleForQuality(float c0Coverage) {
//        if (config.thresholdFine <= c0Coverage) {
//            return GREEN
//        }
//        if (config.thresholdWarning <= c0Coverage) {
//            return YELLOW
//        }
//        return RED
//    }

    def adjustedProjectName() {
        if (project.is(project.rootProject)) {
            "${project.name}:"
        } else {
            int maxLength = 0
            projectsIncludedInTaskGraph().each { p ->
                if (maxLength < p.name.length())
                    maxLength = p.name.length()
            }
            def padding = ""
            (maxLength - project.name.length()).times { padding += " " }
            "${project.name}:${padding}"
        }
    }

    def projectsIncludedInTaskGraph() {
        project.gradle.taskGraph.allTasks.collect { it.project }.unique().sort()
    }

    static Closure writeResult(def headers, def result) {
        def closure = {
            if (headers) {
                headers.each {
                    println it
                }
            }
            println result
        }
        closure
    }

    static def rightAlignedCoverage(float coverage) {
        "${sprintf("%.1f", coverage).padLeft("100.0".length(), ' ')}%"
    }
}
