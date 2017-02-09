package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.CoverageReportConfig
import com.github.ksoichiro.console.reporter.report.CoverageReport
import com.github.ksoichiro.console.reporter.util.Colorizer
import org.gradle.api.GradleException
import org.gradle.api.Project

abstract class CoverageReportWriter<R extends CoverageReport, C extends CoverageReportConfig<R>> extends ReportWriter<R, C> {
    Project project
    boolean colorEnabled
    Map<Project, R> reports
    C config

    @Override
    void write(Project project, Map<Project, R> reports, C config) {
        this.project = project
        this.reports = reports
        this.config = config
        this.colorEnabled = config.colorEnabled

        project.gradle.buildFinished {
            println ""
            println "Coverage summary:"
            def maxLength = reports.keySet().max { it.name.length() }.name.length()
            reports.each { Project p, R report ->
                println toAnsi("${adjustedProjectName(p, maxLength)} ${rightAlignedCoverage(report.c0Coverage)}", report)
            }
        }

        if (config.failIfLessThanThresholdError) {
            reports.each { Project p, R report ->
                if (report.c0Coverage < config.thresholdError) {
                    project.gradle.buildFinished {
                        throw new GradleException(config.brokenCoverageErrorMessage)
                    }
                }
            }
        }
    }

    def toAnsi(message, R report) {
        if (colorEnabled) {
            styleForQuality(report.c0Coverage, message)
        } else {
            message
        }
    }

    def styleForQuality(float c0Coverage, message) {
        if (config.thresholdFine <= c0Coverage) {
            return Colorizer.green(message)
        }
        if (config.thresholdWarning <= c0Coverage) {
            return Colorizer.yellow(message)
        }
        return Colorizer.red(message)
    }

    static adjustedProjectName(Project p, maxLength) {
        def padding = ""
        (maxLength - p.name.length()).times { padding += " " }
        "${p.name}:${padding}"
    }

    static rightAlignedCoverage(float coverage) {
        "${sprintf("%.1f", coverage).padLeft("100.0".length(), ' ')}%"
    }
}
