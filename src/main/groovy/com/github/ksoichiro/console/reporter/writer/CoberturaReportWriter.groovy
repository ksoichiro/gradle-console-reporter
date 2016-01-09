package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.CoberturaReportConfig
import com.github.ksoichiro.console.reporter.report.CoberturaReport
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.gradle.api.Project

import static org.fusesource.jansi.Ansi.Color.*
import static org.fusesource.jansi.Ansi.ansi

class CoberturaReportWriter implements ReportWriter<CoberturaReport, CoberturaReportConfig> {
    @Override
    void write(Project project, CoberturaReport report, CoberturaReportConfig config) {
        if (config.reportAfterBuildFinished) {
            def result = toAnsi(String.format("${adjustedProjectName(project)} %.1f%%", report.c0Coverage), report, config)
            def headers = headerForFirstSubproject(project)
            project.gradle.buildFinished {
                if (headers) {
                    headers.each {
                        println it
                    }
                }
                println result
            }
        } else {
            println toAnsi(String.format("C0 Coverage: %.1f%%", report.c0Coverage), report, config)
        }
    }

    static def headerForFirstSubproject(Project project) {
        if (project.is(project.rootProject)
            || 0 == project.rootProject.subprojects.findIndexOf { it.name == project.name }) {
            return ["", "Coverage summary:"]
        }
        null
    }

    static def toAnsi(def message, CoberturaReport report, CoberturaReportConfig config) {
        AnsiConsole.systemInstall()
        def result = ansi()
            .fg(styleForQuality(report.c0Coverage, config))
            .a(message)
            .reset()
        AnsiConsole.systemUninstall()
        result
    }

    static Ansi.Color styleForQuality(float c0Coverage, CoberturaReportConfig config) {
        if (config.thresholdFine <= c0Coverage) {
            return GREEN
        }
        if (config.thresholdWarning <= c0Coverage) {
            return YELLOW
        }
        return RED
    }

    static def adjustedProjectName(Project project) {
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
