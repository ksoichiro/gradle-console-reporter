package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.CoverageReportConfig
import com.github.ksoichiro.console.reporter.report.CoverageReport
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.gradle.api.GradleException
import org.gradle.api.Project

import static org.fusesource.jansi.Ansi.Color.*
import static org.fusesource.jansi.Ansi.ansi

abstract class CoverageReportWriter<R extends CoverageReport, C extends CoverageReportConfig<R>> implements ReportWriter<R, C> {
    Project project
    R report
    C config

    @Override
    void write(Project project, R report, C config) {
        this.project = project
        this.report = report
        this.config = config
        if (config.reportAfterBuildFinished) {
            def result = toAnsi(String.format("${adjustedProjectName()} %.1f%%", report.c0Coverage))
            def headers = headerForFirstSubproject()
            project.gradle.buildFinished {
                if (headers) {
                    headers.each {
                        println it
                    }
                }
                println result
            }
        } else {
            println toAnsi(String.format("C0 Coverage: %.1f%%", report.c0Coverage))
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
            || 0 == project.rootProject.subprojects.findIndexOf { it.name == project.name }) {
            return ["", "Coverage summary:"]
        }
        null
    }

    def toAnsi(def message) {
        AnsiConsole.systemInstall()
        def result = ansi()
            .fg(styleForQuality(report.c0Coverage))
            .a(message)
            .reset()
        AnsiConsole.systemUninstall()
        result
    }

    Ansi.Color styleForQuality(float c0Coverage) {
        if (config.thresholdFine <= c0Coverage) {
            return GREEN
        }
        if (config.thresholdWarning <= c0Coverage) {
            return YELLOW
        }
        return RED
    }

    def adjustedProjectName() {
        if (project.is(project.rootProject)) {
            "${project.name}:"
        } else {
            int maxLength = 0
            project.rootProject.subprojects.each { p ->
                if (maxLength < p.name.length())
                    maxLength = p.name.length()
            }
            def padding = ""
            (maxLength - project.name.length()).times { padding += " " }
            "${project.name}:${padding}"
        }
    }
}
